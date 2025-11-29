package br.edu.utfpr.pb.pw44s.server.controller;

import br.edu.utfpr.pb.pw44s.server.dto.UserDTO;
import br.edu.utfpr.pb.pw44s.server.model.Authority;
import br.edu.utfpr.pb.pw44s.server.model.User;
import br.edu.utfpr.pb.pw44s.server.service.impl.UserServiceImpl;
import br.edu.utfpr.pb.pw44s.server.shared.GenericResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;

    public UserController(UserServiceImpl userService,
                          ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public GenericResponse createUser(@RequestBody @Valid UserDTO user){

        userService.save(modelMapper.map(user, User.class));

        GenericResponse response = new GenericResponse();
        response.setMessage("User created");
        return response;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/authorities")
    public ResponseEntity<GenericResponse> updateAuthorities(
            @PathVariable Long id,
            @RequestBody List<Authority> authorities) {

        userService.updateAuthorities(id, authorities);

        GenericResponse response = new GenericResponse();
        response.setMessage("Permissões atualizadas com sucesso.");
        return ResponseEntity.ok(response);
    }

}
