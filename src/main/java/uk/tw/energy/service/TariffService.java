package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.Tariff;

import java.util.Collection;
import java.util.List;
@Service
public class TariffService {
    @Autowired
    private final List<Tariff> tariffs;

    public TariffService(List<Tariff> tariffs) {

        this.tariffs = tariffs;
    }

    public Collection<Tariff> findAll() {
        return tariffs;
    }
}
