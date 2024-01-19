package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ServiceResponseDto;
import com.example.inventoryservice.dto.StockDto;
import com.example.inventoryservice.entity.Stock;
import com.example.inventoryservice.feign.BookCatalogInterface;
import com.example.inventoryservice.repo.StockRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StockService {

    @Autowired
    BookCatalogInterface bookCatalogInterface;

    @Autowired
    StockRepo stockRepo;

    @Autowired
    ServiceResponseDto serviceResponseDto;

    @Autowired
    ModelMapper modelMapper;


    //check the availability of the stock
    public Boolean checkStockExists(Integer stockId){
        return stockRepo.existsById(stockId);
    }

    //create or update the stock based on the stock availability
    public ServiceResponseDto CreateStock(StockDto stock){
        //first get the id from the book from bookCatalogService according to the id of the stock
       //going to save the stock with the same id as catalog

        boolean catalogIdExists = bookCatalogInterface.checkCatalogIdExists(stock.getStockId());
        if(catalogIdExists){ //allow creating a stock

            //check a stock is existing
            if(checkStockExists(stock.getStockId())){

                //check whether that catalog is deleted
                boolean isCatalogDeleted = bookCatalogInterface.checkIsCatalogDeleted(stock.getStockId());
                if(isCatalogDeleted){
                    //not allow to update
                    serviceResponseDto.setMessage("stock is not available");
                    serviceResponseDto.setContent(null);
                    return serviceResponseDto;

                }else{
                    boolean checkCatalogDeleted = bookCatalogInterface.checkIsCatalogDeleted(stock.getStockId());
                    if(!checkCatalogDeleted){
                        //stock is existing update it
                        updateExistingStock(stock);
                        serviceResponseDto.setMessage("stock updated");

                        Stock updatedStock = stockRepo.findById(stock.getStockId()).get();
                        serviceResponseDto.setContent(updatedStock);
                        return serviceResponseDto;
                    }else{
                        //say this book is not present
                        serviceResponseDto.setMessage("stock cant be created as there is no book exists");
                        serviceResponseDto.setContent(null);
                        return serviceResponseDto;
                    }
                }

            }else{
                boolean checkCatalogDeleted = bookCatalogInterface.checkIsCatalogDeleted(stock.getStockId());
                if(!checkCatalogDeleted){
                    //create a new stock
                    stockRepo.save(modelMapper.map(stock, Stock.class));
                    serviceResponseDto.setMessage("stock created");
                    serviceResponseDto.setContent(stock);
                    return serviceResponseDto;
                }else{
                    //say this book is not present
                    serviceResponseDto.setMessage("stock cant be created as there is no book exists");
                    serviceResponseDto.setContent(null);
                    return serviceResponseDto;
                }

            }
        }else{
            //say this book is not present
            serviceResponseDto.setMessage("stock cant be created as there is no book exists");
            serviceResponseDto.setContent(null);
            return serviceResponseDto;
        }

    }

    //update the existing stock (call inside the above method)
    public void updateExistingStock(StockDto stock){
        //get book count from the existing stock
        Stock existingStock = stockRepo.findById(stock.getStockId()).get();

        int existingCount = existingStock.getBookCount();
        existingCount += stock.getBookCount();
        existingStock.setBookCount(existingCount);

        stockRepo.save(existingStock);
    }

    //delete books from the stock
    public ServiceResponseDto deleteBooksFromStock(Integer stockId, Integer countToBeRemoved) {
        //check whether the stock is available
        if(checkStockExists(stockId)){
            Stock existingStock = stockRepo.findById(stockId).get();
            int existingBookCount = existingStock.getBookCount();

            if(existingBookCount >= countToBeRemoved){
                existingBookCount-=countToBeRemoved;
                existingStock.setBookCount(existingBookCount);
                stockRepo.save(existingStock);
                serviceResponseDto.setMessage("Requested Count is Removed. Remaining Count: " + existingStock.getBookCount());
                serviceResponseDto.setContent(null);
                return serviceResponseDto;
            }else{
                serviceResponseDto.setMessage("Requested Count Cannot be deleted. Existing Count = " + existingBookCount);
                serviceResponseDto.setContent(null);
                return serviceResponseDto;
            }
        }else{
            serviceResponseDto.setMessage("Stock is Not available for given id");
            serviceResponseDto.setContent(null);
            return serviceResponseDto;
        }
    }


    //check whether a stock is 0 before give permission to catalog to delete a particular catalog
    public Boolean checkStockBeforeDeleting(Integer stockId){
        Stock existingStock = stockRepo.findById(stockId).get();
        if(existingStock.getBookCount() == 0 ){
            //we give permission to delete a catalog
            return true;
        }else if(existingStock == null){
            return true;
        }
        else{
            //we do not give permission to delete a catalog
            return false;
        }
    }
}
