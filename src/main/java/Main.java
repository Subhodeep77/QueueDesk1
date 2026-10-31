import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        List<Ticket> tickets = new ArrayList<>();

        BugTicket bug = new BugTicket(
                BugTicket.Severity.CRITICAL,
                "Login fails with valid credentials",
                "Login failure",
                "Users cannot log in",
                "Subho",
                Ticket.Priority.HIGH);

        AccessRequestTicket access = new AccessRequestTicket(
                "Production Dashboard",
                AccessRequestTicket.AccessLevel.ADMIN,
                "Admin access",
                "Requesting administrator access",
                "Subho",
                Ticket.Priority.MEDIUM);

        FeatureRequestTicket feature = new FeatureRequestTicket(
                25,
                "Improve user productivity",
                "Dashboard filters",
                "Add date-based filtering",
                "Subho",
                Ticket.Priority.LOW);

        bug.escalate();

        tickets.add(bug);
        tickets.add(access);
        tickets.add(feature);

        // Existing TriageBoard
        System.out.println("\n========== TRIAGE BOARD ==========");
        TriageBoard.printTriageBoard(tickets);


        // ==========================================
        // TICKET ANALYTICS TESTING
        // ==========================================

        System.out.println("\n========== TICKET ANALYTICS ==========");

        // 1. Count tickets by priority
        Map<Ticket.Priority, Long> priorityCount =
                TicketAnalytics.countByPriority(tickets);

        System.out.println("\nTickets by priority:");
        priorityCount.forEach((priority, count) ->
                System.out.println(priority + " : " + count));


        // 2. Group tickets by assignee
        Map<String, List<Ticket>> assigneeGroups =
                TicketAnalytics.groupByAssignee(tickets);

        System.out.println("\nTickets grouped by assignee:");
        assigneeGroups.forEach((assignee, assignedTickets) -> {
            System.out.println("\nAssignee: " + assignee);

            assignedTickets.forEach(ticket ->
                    System.out.println("  " + ticket.getTitle()));
        });


        // 3. Average effort hours
        double averageEffort =
                TicketAnalytics.averageEffortHours(tickets);

        System.out.println("\nAverage effort hours: " + averageEffort);


        // 4. Total effort hours
        double totalEffort =
                TicketAnalytics.totalEffortHours(tickets);

        System.out.println("Total effort hours: " + totalEffort);


        // 5. Oldest open ticket
        Optional<Ticket> oldestOpen =
                TicketAnalytics.oldestOpenTicket(tickets);

        System.out.println("\nOldest open ticket:");

        oldestOpen.ifPresentOrElse(
                ticket -> System.out.println(ticket.getTitle()),
                () -> System.out.println("No open tickets found.")
        );


        // 6. Combined unique tag line
        String tagLine = TicketAnalytics.tagLine(tickets);

        System.out.println("\nAll unique tags:");
        System.out.println(tagLine);


        // ==========================================
        // REPOSITORY TESTING
        // ==========================================

        System.out.println("\n========== REPOSITORY TESTING ==========");

        Repository<Ticket> repository = new Repository<>();

        for(Ticket ticket : tickets) {
                repository.add(ticket);
        }

        // 7. findByIdSafe() with map()
        System.out.println("\nTesting findByIdSafe() with map():");

        String ticketTitle = repository.findByIdSafe(bug.getId())
                .map(Ticket::getTitle)
                .orElse("Ticket not found");

        System.out.println("Ticket title: " + ticketTitle);


        // 8. findByIdSafe() with ifPresentOrElse()
        System.out.println("\nTesting findByIdSafe() with ifPresentOrElse():");

        repository.findByIdSafe(access.getId())
                .ifPresentOrElse(
                        ticket -> System.out.println(
                                "Found ticket: " + ticket.getTitle()),
                        () -> System.out.println("Ticket not found.")
                );


        // 9. findByIdSafe() with orElseThrow()
        System.out.println("\nTesting findByIdSafe() with orElseThrow():");

        try {
            Ticket ticket = repository.findByIdSafe(feature.getId())
                    .orElseThrow(() ->
                            new TicketNotFoundException("Ticket not found having id: " + feature.getId() + "."));

            System.out.println("Found ticket: " + ticket.getTitle());

        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }


        // 10. Testing missing ticket using Optional.empty()
        System.out.println("\nTesting missing ticket:");

        repository.findByIdSafe(999)
                .ifPresentOrElse(
                        ticket -> System.out.println(ticket.getTitle()),
                        () -> System.out.println("Ticket with ID 999 not found.")
                );


        // ==========================================
        // EXISTING FILE OPERATIONS
        // ==========================================

        System.out.println("\n========== FILE OPERATIONS ==========");

        TicketFileLoader loader = new TicketFileLoader();

        try {
            List<Ticket> collTickets = loader.load(Path.of("tickets.txt"));

            for (Ticket t : collTickets) {
                System.out.println(t);
            }

            try {
                TicketReportGenerator.generateReport(
                        collTickets, Path.of("report.txt"));

            } catch (IOException e) {
                System.out.println("File writing error: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("File reading error: " + e.getMessage());

        } catch (InvalidTicketDataException e) {
            System.out.println("Invalid ticket file: " + e.getMessage());
        }
    }
}