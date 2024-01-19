package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ResponseDto;
import com.example.inventoryservice.dto.ServiceResponseDto;
import com.example.inventoryservice.dto.StockDto;
import com.example.inventoryservice.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/inventory")
public class StockController {

    @Autowired
    private ResponseDto responseDto;

    @Autowired
    private StockService stockService;

    //create stock
    @PostMapping("/createStock") //add books to an existing stock or new stock
    public ResponseEntity<ResponseDto> createStock(@RequestBody StockDto stock){
        try{
            ServiceResponseDto res =  stockService.CreateStock(stock);
            responseDto.setMessage(res.getMessage());
            responseDto.setContent(res.getContent());
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch (Exception ex){
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(ex);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/deleteBooksFromStock") //delete books from the stock
    public ResponseEntity<ResponseDto> deleteBooksFromStock(@RequestParam Integer stockId, @RequestParam Integer countToBeRemoved){
       try{
           ServiceResponseDto res = stockService.deleteBooksFromStock(stockId, countToBeRemoved);
           responseDto.setMessage(res.getMessage());
           responseDto.setContent(res.getContent());
           return  new ResponseEntity<>(responseDto, HttpStatus.OK);
       }catch (Exception ex){
           responseDto.setMessage(ex.getMessage());
           responseDto.setContent(ex);
           return  new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
       }
    }


    //check the existence of a Stock
    @GetMapping("/checkStockAvailability/{stockId}")
    public Boolean checkStockAvailability(@PathVariable Integer stockId){
        return stockService.checkStockExists(stockId);
    }


    //check the stock and give the permission to delete a catalog
    @GetMapping("/checkStockLevelsBeforeDeleting/{catalogId}")
    public Boolean checkStockLevelsBeforeDeleting(@PathVariable Integer catalogId){
        Boolean permission =  stockService.checkStockBeforeDeleting(catalogId);
        return permission;
    }

}
