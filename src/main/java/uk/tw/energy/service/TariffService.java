package uk.tw.energy.service;

import uk.tw.energy.domain.Tariff;

import java.util.Collection;
import java.util.List;

public class TariffService {
    private final List<Tariff> tariffs;

    public TariffService(List<Tariff> tariffs) {

        this.tariffs = tariffs;
    }

    public Collection<Tariff> findAll() {
        return tariffs;
    }
}
