package br.edu.utfpr.pb.pw44s.server.service;

import br.edu.utfpr.pb.pw44s.server.dto.AttachmentResponseDTO;
import br.edu.utfpr.pb.pw44s.server.dto.OrdersDTO;
import br.edu.utfpr.pb.pw44s.server.model.OrderAttachment;
import br.edu.utfpr.pb.pw44s.server.model.Orders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IOrdersService extends ICrudService<Orders, Long> {
    OrdersDTO createOrder(OrdersDTO orderDTO);
    List<OrdersDTO> findByUser();
    OrdersDTO updateStatus(Long id, String status);
    OrdersDTO updateOrderAddress(Long orderId, Long addressId);
    List<OrdersDTO> findAllAdmin();

    void uploadAttachment(Long orderId, MultipartFile file);
    List<AttachmentResponseDTO> getAttachments(Long orderId);
    OrderAttachment getAttachmentById(Long attachmentId);
}
