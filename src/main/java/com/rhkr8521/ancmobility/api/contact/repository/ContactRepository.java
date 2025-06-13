package com.rhkr8521.ancmobility.api.contact.repository;

import com.rhkr8521.ancmobility.api.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
