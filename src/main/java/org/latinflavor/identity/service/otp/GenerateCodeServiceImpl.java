package org.latinflavor.identity.service.otp;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.adapter.persistence.user.UserJpaAdapter;
import org.latinflavor.identity.adapter.persistence.user.UserRepository;
import org.latinflavor.identity.adapter.persistence.user.port.UserSearchPort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GenerateCodeServiceImpl implements GenerateOtpService {


    @Override
    public void generateCode(String email) {

    }


}
