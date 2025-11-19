package com.prai.smpp_api.repository;

import com.prai.smpp_api.entity.DeliveryReceiptEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryReceiptRepository extends CrudRepository<DeliveryReceiptEntity, String> {

}
