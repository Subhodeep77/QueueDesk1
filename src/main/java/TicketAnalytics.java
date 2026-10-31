import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class TicketAnalytics {
    private TicketAnalytics() {
    }

    public static Map<Ticket.Priority, Long> countByPriority(List<Ticket> tickets) {
        if (tickets == null) {
            throw new IllegalArgumentException("Tickets list cannot be null.");
        }

        return tickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getPriority,
                        Collectors.counting()));
    }

    public static Map<String,List<Ticket>> groupByAssignee(List<Ticket> tickets) {
        if(tickets == null) {
            throw new IllegalArgumentException("Ticket list cannot be null.");
        } 
        return tickets.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.getAssignee() == null ? "Unassigned" : ticket.getAssignee()));
    }

    public static double averageEffortHours(List<Ticket> tickets) {
        if(tickets == null) throw new IllegalArgumentException("Ticket list cannot be null.");
        return tickets.stream()
                .mapToDouble(Ticket::estimateEffortHours)
                .average()
                .orElse(0.0);
    }

    public static double totalEffortHours(List<Ticket> tickets) {
        if(tickets == null) throw new IllegalArgumentException("Ticket list cannot be null.");
        return tickets.stream()
                .map(Ticket::estimateEffortHours)
                .reduce(0.0,Double::sum,Double::sum);
    }

    public static Optional<Ticket> oldestOpenTicket(List<Ticket> tickets) {
        if(tickets == null) throw new IllegalArgumentException("Ticket list cannot be null.");
        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.OPEN)
                .min(Comparator.comparing(Ticket::getCreatedAt));
    }

    public static String tagLine(List<Ticket> tickets) {
        if(tickets == null) throw new IllegalArgumentException("Ticket list cannot be null.");
        return tickets.stream()
               .map(Ticket::getTags)
               .flatMap(tags -> tags.stream())
               .distinct()
               .sorted()
               .collect(Collectors.joining(", "));
    }

}