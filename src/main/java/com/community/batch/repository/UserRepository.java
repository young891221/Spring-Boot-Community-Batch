package com.community.batch.repository;

import com.community.batch.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by youngjae on 2018. 3. 3..
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
}
