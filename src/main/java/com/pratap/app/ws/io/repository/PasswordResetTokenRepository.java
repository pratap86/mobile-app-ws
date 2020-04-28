package com.pratap.app.ws.io.repository;

import org.springframework.data.repository.CrudRepository;

import com.pratap.app.ws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

}
