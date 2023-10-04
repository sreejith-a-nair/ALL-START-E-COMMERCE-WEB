package com.mydemo.demoproject.Repository.admin;

import com.mydemo.demoproject.Entity.Offer;
import com.mydemo.demoproject.service.admin.offer.OfferService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfferRepo extends JpaRepository<Offer,UUID> {


}
