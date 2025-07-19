package org.wgtech.wgmall_backend.service;

import org.wgtech.wgmall_backend.entity.Address;

public interface AddressService {
    Address getAddressByUserId(Long userId);

    Address saveOrUpdateAddress(Long userId, Address addressData);

    Address setOrUpdateAddress(Long userId, Address address);

    void deleteAddressByUserId(Long userId);

}
