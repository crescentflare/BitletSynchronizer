package com.crescentflare.bitletsynchronizerexample.model.usage;

/**
 * Usage model: usage value item
 * An item in the list of usage overview values
 */
public class UsageItem
{
    // ---
    // Members
    // ---

    private float amount;
    private UsageUnit unit;
    private String label;


    // ---
    // Generated code
    // ---

    public float getAmount()
    {
        return amount;
    }

    public void setAmount(float amount)
    {
        this.amount = amount;
    }

    public UsageUnit getUnit()
    {
        return unit;
    }

    public void setUnit(UsageUnit unit)
    {
        this.unit = unit;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return "UsageItem{" +
                "amount=" + amount +
                ", unit=" + unit +
                ", label='" + label + '\'' +
                '}';
    }
}
