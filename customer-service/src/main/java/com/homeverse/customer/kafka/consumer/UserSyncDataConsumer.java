package com.homeverse.customer.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeverse.common.kafka.cdc.DebeziumMessage;
import com.homeverse.common.kafka.cdc.message.UserCdcMessage;
import com.homeverse.customer.entity.Customer;
import com.homeverse.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSyncDataConsumer {

    private final ObjectMapper objectMapper;
    private final CustomerRepository customerRepository;

    @KafkaListener(topics = "identity_server.public.user_credentials", groupId = "customer-group")
    @Transactional
    public void consume(String message) {
        try {
            // 1. Đọc String thành Cây JSON
            JsonNode rootNode = objectMapper.readTree(message);

            // 2. Lột vỏ "payload" (nếu có) để lấy đúng phần lõi dữ liệu
            JsonNode payloadNode = rootNode.has("payload") ? rootNode.get("payload") : rootNode;


            // 3. Map cái lõi đó vào class DebeziumMessage của bạn
            DebeziumMessage<UserCdcMessage> debeziumMessage = objectMapper.convertValue(
                    payloadNode,
                    new TypeReference<DebeziumMessage<UserCdcMessage>>() {}
            );

            // Bỏ qua các tín hiệu rác
            if (debeziumMessage == null || debeziumMessage.getOp() == null) return;

            String operation = debeziumMessage.getOp();
            UserCdcMessage payload = debeziumMessage.getAfter();

            // Khi một User mới được tạo (op = 'c') hoặc được đồng bộ lần đầu (op = 'r')
            if (("c".equals(operation) || "r".equals(operation)) && payload != null) {
                log.info(" BẮT ĐƯỢC TÀI KHOẢN TỪ IDENTITY: ID={}, Name={}, Email={}",
                        payload.getId(), payload.getFullName(), payload.getEmail());

                if (!customerRepository.existsById(payload.getId())) {
                    Customer newCustomer = Customer.builder()
                            .id(payload.getId())
                            .email(payload.getEmail())
                            .fullName(payload.getFullName())
                            .phone(payload.getPhone())
                            .kycStatus("UNVERIFIED")
                            .build();

                    customerRepository.save(newCustomer);
                    log.info("Đã tạo hồ sơ Customer thành công!");
                } else {
                    log.info("Customer ID={} đã tồn tại, bỏ qua tạo mới.", payload.getId());
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý thông điệp CDC: {}", e.getMessage(), e);
        }
    }
}