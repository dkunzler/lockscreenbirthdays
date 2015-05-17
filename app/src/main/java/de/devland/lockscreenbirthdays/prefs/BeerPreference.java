package de.devland.lockscreenbirthdays.prefs;

import android.app.Activity;
import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import de.devland.lockscreenbirthdays.util.IabHelper;
import de.devland.lockscreenbirthdays.util.IabResult;
import de.devland.lockscreenbirthdays.util.Inventory;
import de.devland.lockscreenbirthdays.util.Purchase;

/**
 * Created by deekay on 23/01/15.
 */
public class BeerPreference extends ListPreference implements
        IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabPurchaseFinishedListener {

    private IabHelper iabHelper;
    private Activity activity;

    public BeerPreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BeerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BeerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BeerPreference(Context context) {
        super(context);
    }

    public void init(IabHelper iabHelper, Activity activity) {
        this.iabHelper = iabHelper;
        this.activity = activity;
        List<String> skus = new ArrayList<>();
        for (CharSequence sku : getEntries()) {
            skus.add(sku.toString());
        }
        iabHelper.queryInventoryAsync(true, skus, this);
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isSuccess()) {
            CharSequence[] entryValues = getEntries();
            CharSequence[] newValues = new CharSequence[entryValues.length];
            for (int i = 0; i < entryValues.length; i++) {
                CharSequence donation = entryValues[i];
                String price = inv.getSkuDetails(donation.toString()).getPrice();
                newValues[i] = donation + " (" + price + ")";
            }
            setEntryValues(newValues);
        }
    }

    @Override
    protected boolean callChangeListener(Object newValue) {
        boolean result = super.callChangeListener(newValue);
        iabHelper.launchPurchaseFlow(activity, newValue.toString(), 1, this);
        return result;
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {

    }
}
