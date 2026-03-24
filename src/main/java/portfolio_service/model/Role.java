
package portfolio_service.model;

public enum Role {
    INVESTOR,   // can only view their own portfolio
    ADVISOR,    // can view and rebalance any portfolio
    ADMIN       // full access to everything
}