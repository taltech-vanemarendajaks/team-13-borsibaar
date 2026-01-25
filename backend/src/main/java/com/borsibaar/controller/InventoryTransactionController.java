package com.borsibaar.controller;

import com.borsibaar.dto.InventoryTransactionResponseDto;
import com.borsibaar.entity.User;
import com.borsibaar.repository.InventoryTransactionRepository;
import com.borsibaar.service.InventoryService;
import com.borsibaar.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-transaction")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryService inventoryService;

    /**
     * Exposes InventoryTransaction data per product and organization
     * @param productId
     * @param organizationId
     * @return
     */
    @GetMapping
    public List<InventoryTransactionResponseDto> getProductTransactionHistory(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long organizationId) {

        Long orgId;
        if (organizationId != null) {
            orgId = organizationId;
        } else {
            User user = SecurityUtils.getCurrentUser();
            orgId = user.getOrganizationId();
        }
        return inventoryService.getTransactionHistory(productId, orgId);
    }
}