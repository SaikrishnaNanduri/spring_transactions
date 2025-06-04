package com.spring.transactions.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
 private Long id;
 private String accountNumber;
 private String ownerName;
 private BigDecimal balance;
}
