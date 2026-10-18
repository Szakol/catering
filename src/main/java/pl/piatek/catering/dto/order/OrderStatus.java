package pl.piatek.catering.dto.order;

public enum OrderStatus {
    NEW, CONFIRMED, IN_PROGRESS, READY_FOR_PICKUP, OUT_FOR_DELIVERY, COMPLETED, CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case NEW -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == READY_FOR_PICKUP || target == OUT_FOR_DELIVERY || target == CANCELLED;
            case READY_FOR_PICKUP, OUT_FOR_DELIVERY -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}