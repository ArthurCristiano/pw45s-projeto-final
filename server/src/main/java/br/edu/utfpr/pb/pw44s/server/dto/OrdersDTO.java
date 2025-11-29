package br.edu.utfpr.pb.pw44s.server.dto;

import br.edu.utfpr.pb.pw44s.server.model.Orders;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDTO {
    private Long id;
    private LocalDateTime date;
    private String status;
    private UserDTO user;
    private List<OrderItemDTO> items;
    private Long addressId;

    public OrdersDTO(Orders order) {
        this.id = order.getId();
        this.date = order.getDate();
        this.status = order.getStatus() != null ? order.getStatus().toString() : null;

        if (order.getUser() != null) {
            this.user = new UserDTO(order.getUser());
        }
    }
}
