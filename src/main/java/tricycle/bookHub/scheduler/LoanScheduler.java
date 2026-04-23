package tricycle.bookHub.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tricycle.bookHub.model.Etat;
import tricycle.bookHub.model.Statut;
import tricycle.bookHub.repository.LoanRepository;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanScheduler {

    private final LoanRepository loanRepository;

    // Tourne tous les jours à minuit
    //@Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkOverdueLoans() {
        log.info("== Vérification des retards ==");

        List<tricycle.bookHub.model.Loan> overdueLoans = loanRepository
                .findByStatusAndReturnDateBefore(Statut.EN_COURS, new Date());

        overdueLoans.forEach(loan -> {
            loan.setStatus(Statut.RETARD);
            loan.getBooks().setState(Etat.RETARD);
            log.info("Emprunt {} passé en RETARD (user: {}, livre: {})",
                    loan.getId(),
                    loan.getUser().getEmail(),
                    loan.getBooks().getTitle()
            );
        });

        loanRepository.saveAll(overdueLoans);
        log.info("== {} emprunt(s) mis en retard ==", overdueLoans.size());
    }
}