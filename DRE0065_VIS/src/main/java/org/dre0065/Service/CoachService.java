package org.dre0065.Service;

import org.dre0065.Model.Coach;
import org.dre0065.Repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    public void saveAllCoaches(List<Coach> coaches) {
        coachRepository.saveAll(coaches);
    }

    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }
}
