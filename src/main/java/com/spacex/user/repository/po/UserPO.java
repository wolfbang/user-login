package com.spacex.user.repository.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user")
public class UserPO {
    @Id
    private Long id;
    private String account;
    private String name;
    private String password;
    private String passwordSalt;
    private Integer status;

    private Date lastLoginTime;
    private Date createdTime;
    private Date updatedTime;
}
