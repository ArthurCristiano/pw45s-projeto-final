package br.edu.utfpr.pb.pw44s.server.service;

import br.edu.utfpr.pb.pw44s.server.dto.UserDTO;
import br.edu.utfpr.pb.pw44s.server.model.Authority;

import java.util.List;

public interface IUserService {
    UserDTO updateAuthorities(Long id, List<Authority> authorities);
}
