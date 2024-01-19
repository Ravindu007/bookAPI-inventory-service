package com.example.inventoryservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("BOOK-CATALOG-SERVICE")
public interface BookCatalogInterface {

    @GetMapping(value = "api/v1/bookCatalog/getSingleCatalog/{catalogId}")
    public Boolean checkCatalogIdExists(@PathVariable Integer catalogId);


    @GetMapping(value = "api/v1/bookCatalog/checkIsCatalogDeleted/{catalogId}")
    public Boolean checkIsCatalogDeleted(@PathVariable Integer catalogId);
}
