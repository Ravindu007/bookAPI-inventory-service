package com.example.inventoryservice.repo;

import com.example.inventoryservice.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepo extends JpaRepository<Stock, Integer> {
}
