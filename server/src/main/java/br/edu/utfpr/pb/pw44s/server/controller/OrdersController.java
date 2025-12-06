package br.edu.utfpr.pb.pw44s.server.controller;

import br.edu.utfpr.pb.pw44s.server.dto.AttachmentResponseDTO;
import br.edu.utfpr.pb.pw44s.server.dto.OrdersDTO;
import br.edu.utfpr.pb.pw44s.server.model.OrderAttachment;
import br.edu.utfpr.pb.pw44s.server.model.Orders;
import br.edu.utfpr.pb.pw44s.server.service.ICrudService;
import br.edu.utfpr.pb.pw44s.server.service.IOrdersService;
import br.edu.utfpr.pb.pw44s.server.shared.GenericResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrdersController extends CrudController<Orders, OrdersDTO, Long>{

    private final IOrdersService ordersService;
    private final ModelMapper modelMapper;

    public OrdersController(IOrdersService ordersService, ModelMapper modelMapper) {
        super(Orders.class, OrdersDTO.class);
        this.ordersService = ordersService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected ICrudService<Orders, Long> getService(){
        return this.ordersService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<OrdersDTO> create(@RequestBody @Valid OrdersDTO orderDTO) {
        try {
            OrdersDTO createdOrder = ordersService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrdersDTO>> findByUser() {
        try {
            List<OrdersDTO> orders = ordersService.findByUser();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrdersDTO> updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            String cleanStatus = status.replaceAll("\"", "");
            OrdersDTO updatedOrder = ordersService.updateStatus(id, cleanStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/address/{addressId}")
    public ResponseEntity<OrdersDTO> updateAddress(@PathVariable Long orderId, @PathVariable Long addressId) {
        try {
            OrdersDTO updatedOrder = ordersService.updateOrderAddress(orderId, addressId);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<GenericResponse> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        ordersService.uploadAttachment(id, file);

        GenericResponse response = new GenericResponse();
        response.setMessage("Arquivo enviado com sucesso!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> getAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(ordersService.getAttachments(id));
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long attachmentId) {
        OrderAttachment attachment = ordersService.getAttachmentById(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

}
