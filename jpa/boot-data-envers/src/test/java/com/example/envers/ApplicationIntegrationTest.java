package com.example.envers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.envers.common.AbstractIntegrationTest;
import com.example.envers.entities.Customer;
import com.example.envers.repositories.CustomerRepository;
import java.util.Iterator;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void initialRevision() {
        Customer cust = new Customer();
        cust.setName("junit");
        cust.setAddress("address");
        Customer customer = customerRepository.save(cust);

        Revisions<Integer, Customer> revisions = customerRepository.findRevisions(customer.getId());

        assertThat(revisions).isNotEmpty().allSatisfy(revision -> assertThat(revision.getEntity())
                .extracting(Customer::getId, Customer::getName, Customer::getVersion)
                .containsExactly(customer.getId(), customer.getName(), customer.getVersion()));
    }

    @Test
    void updateIncreasesRevisionNumber() {
        var cust = new Customer();
        cust.setName("text");
        Customer customer = customerRepository.save(cust);

        customer.setName("If");

        customerRepository.save(customer);

        Optional<Revision<Integer, Customer>> revision = customerRepository.findLastChangeRevision(customer.getId());

        assertThat(revision)
                .isPresent()
                .hasValueSatisfying(rev ->
                        assertThat(rev.getRevisionNumber()).isPresent().get().isEqualTo(5))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity())
                        .extracting(Customer::getName)
                        .asString()
                        .isEqualTo("If"));
    }

    @Test
    void deletedItemWillHaveRevisionRetained() {
        var cust = new Customer();
        cust.setName("junit");
        Customer customer = customerRepository.save(cust);

        customerRepository.delete(customer);

        Revisions<Integer, Customer> revisions = customerRepository.findRevisions(customer.getId());

        assertThat(revisions).hasSize(2);

        Iterator<Revision<Integer, Customer>> iterator = revisions.iterator();

        Revision<Integer, Customer> initialRevision = iterator.next();
        Revision<Integer, Customer> finalRevision = iterator.next();

        assertThat(initialRevision).satisfies(rev -> assertThat(rev.getEntity())
                .extracting(Customer::getId, Customer::getName, Customer::getVersion)
                .containsExactly(customer.getId(), customer.getName(), customer.getVersion()));

        assertThat(finalRevision).satisfies(rev -> assertThat(rev.getEntity())
                .extracting(Customer::getId, Customer::getName, Customer::getVersion)
                .containsExactly(customer.getId(), null, (short) 0));
    }
}
