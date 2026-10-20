package tiermart.model.membership;

/**
 * The tier thresholds, in one place.
 *
 * <p>A customer's tier is driven by lifetime spend (the money actually paid,
 * after discounts). Keeping the numbers here -- rather than scattered through
 * the state classes -- means the whole promotion ladder can be read at a glance
 * and changed in one spot.
 *
 * <pre>
 *   Basic     $0        0%   -> Silver   at $100
 *   Silver    $100      5%   -> Gold     at $500   (back to Basic below $100)
 *   Gold      $500     10%   -> Platinum at $1000  (back to Silver below $500)
 *   Platinum  $1000    15%                         (back to Gold below $1000)
 * </pre>
 */
public final class Tiers {
    private Tiers() {}

    public static final long SILVER_AT_CENTS   = 100_00;    // $100
    public static final long GOLD_AT_CENTS     = 500_00;    // $500
    public static final long PLATINUM_AT_CENTS = 1_000_00;  // $1000

    public static final double BASIC_RATE    = 0.00;
    public static final double SILVER_RATE   = 0.05;
    public static final double GOLD_RATE     = 0.10;
    public static final double PLATINUM_RATE = 0.15;
}
