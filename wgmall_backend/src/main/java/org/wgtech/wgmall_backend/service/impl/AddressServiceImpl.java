package org.wgtech.wgmall_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wgtech.wgmall_backend.entity.Address;
import org.wgtech.wgmall_backend.entity.User;
import org.wgtech.wgmall_backend.repository.AddressRepository;
import org.wgtech.wgmall_backend.repository.UserRepository;
import org.wgtech.wgmall_backend.service.AddressService;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Address getAddressByUserId(Long userId) {
        return addressRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("用户未设置地址"));
    }

    @Override
    public Address saveOrUpdateAddress(Long userId, Address addressData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Address address = user.getAddress();
        if (address == null) {
            address = new Address(); // 自动新建
        }

        // 统一设置字段
        address.setProvince(addressData.getProvince());
        address.setCity(addressData.getCity());
        address.setDistrict(addressData.getDistrict());
        address.setStreet(addressData.getStreet());
        address.setDetail(addressData.getDetail());
        address.setCountry(addressData.getCountry());
        address.setReceiverName(addressData.getReceiverName());
        address.setReceiverPhone(addressData.getReceiverPhone());

        // 保存地址
        Address savedAddress = addressRepository.save(address);

        // 如果是第一次设置，需要关联到用户
        if (user.getAddress() == null) {
            user.setAddress(savedAddress);
            userRepository.save(user);
        }

        return savedAddress;
    }
}
