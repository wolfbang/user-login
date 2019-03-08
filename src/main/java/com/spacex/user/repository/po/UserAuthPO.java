package com.spacex.user.repository.po;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class UserAuthPO {
    @Id
    private Long id;
    private Long userId;
    private Integer type;
    private String account;
    private Date createdTime;
    private Date updatedTime;
}
