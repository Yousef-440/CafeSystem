package com.CafeSystem.cafe.service.serviceImpl;

import com.CafeSystem.cafe.dto.bill.BillRequestDTO;
import com.CafeSystem.cafe.dto.bill.ProductDetailsDTO;
import com.CafeSystem.cafe.model.Bill;
import com.CafeSystem.cafe.repository.BillRepository;
import com.CafeSystem.cafe.repository.CategoryRepository;
import com.CafeSystem.cafe.service.BillService;
import com.CafeSystem.cafe.utils.BillPdfGenerator;
import com.CafeSystem.cafe.utils.CafeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;
    @Value("${store.location}")
    private String location;

    @Autowired
    private BillPdfGenerator billPdfGenerator;

    @Override
    public ResponseEntity<String> generateReport(BillRequestDTO billRequestDTO, boolean isGenerate) {
        log.info("generateReport started with isGenerate: {}", isGenerate);
        log.debug("Received BillRequestDTO: {}", billRequestDTO);

        try {
            Bill bill = convertDtoToEntity(billRequestDTO);

            for (ProductDetailsDTO product : billRequestDTO.getProductDetails()) {
                if (categoryRepository.findByName(product.getCategory()).isPresent()) {
                    continue;
                }
                String msg = "Sorry, Category Name => {" + product.getCategory() + "} does not exist";
                return ResponseEntity.badRequest().body(
                        new ObjectMapper().writeValueAsString(Map.of("message", msg))
                );
            }

            log.debug("Converted Bill entity: {}", bill);

            billRepository.save(bill);
            log.info("Bill saved successfully with UUID: {}", bill.getUuid());

            byte[] pdfBytes = billPdfGenerator.generatePdf(bill);

            try (FileOutputStream fos = new FileOutputStream(location + bill.getId() + ".pdf")) {
                fos.write(pdfBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return CafeUtil.getResponseEntity("Report generated successfully", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception occurred while generating report", e);
            return CafeUtil.getResponseEntity("Something Went Wrong", HttpStatus.BAD_REQUEST);
        }
    }

    public Bill convertDtoToEntity(BillRequestDTO billRequestDTO) {
        String productDetailsJson = "";
        try {
            productDetailsJson = objectMapper.writeValueAsString(billRequestDTO.getProductDetails());
            log.debug("Serialized productDetails: {}", productDetailsJson);
        } catch (Exception e) {
            log.error("Error while serializing productDetails", e);
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String uuid = UUID.randomUUID().toString();

        log.debug("Generating Bill entity with UUID: {} by user: {}", uuid, currentUser);

        return Bill.builder()
                .uuid(uuid)
                .name(billRequestDTO.getName())
                .email(billRequestDTO.getEmail())
                .contactNumber(billRequestDTO.getContactNumber())
                .paymentMethod(billRequestDTO.getPaymentMethod())
                .productDetails(productDetailsJson)
                .createdBy(currentUser)
                .build();
    }
}
