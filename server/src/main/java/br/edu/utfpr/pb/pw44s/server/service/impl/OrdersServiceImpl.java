package br.edu.utfpr.pb.pw44s.server.service.impl;

import br.edu.utfpr.pb.pw44s.server.dto.OrderItemDTO;
import br.edu.utfpr.pb.pw44s.server.dto.OrdersDTO;
import br.edu.utfpr.pb.pw44s.server.dto.UserDTO;
import br.edu.utfpr.pb.pw44s.server.model.*;
import br.edu.utfpr.pb.pw44s.server.repository.*;
import br.edu.utfpr.pb.pw44s.server.service.EmailService;
import br.edu.utfpr.pb.pw44s.server.service.IOrdersService;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends CrudServiceImpl<Orders, Long> implements IOrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderItensRepository orderItensRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final EmailService emailService;

    public OrdersServiceImpl(OrdersRepository ordersRepository,
                             OrderItensRepository orderItensRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             ModelMapper modelMapper,
                             AddressRepository addressRepository,
                             EmailService emailService) {
        this.ordersRepository = ordersRepository;
        this.orderItensRepository = orderItensRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.emailService = emailService;
    }

    @Override
    protected JpaRepository<Orders, Long> getRepository() {
        return this.ordersRepository;
    }

    @Override
    @Transactional
    public Orders save(Orders entity) {
        return getRepository().save(entity);
    }

    public List<OrdersDTO> findAllAdmin() {
        return ordersRepository.findAll().stream()
                .map(OrdersDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdersDTO createOrder(OrdersDTO orderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        Orders order = new Orders();
        order.setDate(LocalDateTime.now());
        order.setUser(user);
        order.setAddress_id(null);
        order.setStatus("AGUARDANDO_PAGAMENTO");
        Orders savedOrder = ordersRepository.save(order);

        List<OrderItens> items = orderDTO.getItems().stream().map(itemDTO -> {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProductId()));

            OrderItens item = new OrderItens();
            item.setOrders(savedOrder);
            item.setProduct(product);
            item.setPrice(itemDTO.getPrice());
            item.setQuantity(itemDTO.getQuantity());

            return item;
        }).collect(Collectors.toList());

        orderItensRepository.saveAll(items);

        orderDTO.setId(savedOrder.getId());
        orderDTO.setDate(savedOrder.getDate());
        orderDTO.setUser(new UserDTO(user));

        return orderDTO;
    }

    @Override
    public List<OrdersDTO> findByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        return ordersRepository.findByUser(user).stream()
                .map(order -> {
                    OrdersDTO dto = modelMapper.map(order, OrdersDTO.class);

                    dto.setUser(new UserDTO(user));

                    List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                        return OrderItemDTO.builder()
                                .productId(item.getProduct().getId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .build();
                    }).collect(Collectors.toList());

                    dto.setItems(itemDTOs);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrdersDTO updateStatus(Long id, String status) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        order.setStatus(status);
        Orders updatedOrder = ordersRepository.save(order);

        if (updatedOrder.getUser() != null && updatedOrder.getUser().getEmail() != null) {
            new Thread(() -> {
                emailService.sendStatusChangeEmail(
                        updatedOrder.getUser().getEmail(),
                        updatedOrder.getId(),
                        updatedOrder.getStatus()
                );
            }).start();
        }

        return new OrdersDTO(updatedOrder);
    }

    @Transactional
    public OrdersDTO updateOrderAddress(Long orderId, Long addressId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado!"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado!"));

        order.setAddress_id(address);
        Orders updatedOrder = ordersRepository.save(order);

        return new OrdersDTO(updatedOrder);
    }
}