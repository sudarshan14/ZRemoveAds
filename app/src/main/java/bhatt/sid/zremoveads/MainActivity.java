package bhatt.sid.zremoveads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;

public class MainActivity extends AppCompatActivity {

    private String progressText = "";
    private static final String TAG =
            "InAppBilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased"; //"removead";
    Button buyItem;
    Button enableBuy;
    Button moveNext;
    Button checkPalindrome;
    boolean isAdDisable = false;
    private TextView progressTextView;
    EditText input;


    @Override
    protected void onResume() {
        super.onResume();
//        queryPurchasedItems();
    }


    private void showAd() {
        if (!isAdDisable) {
            final AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressTextView = (TextView) findViewById(R.id.textViewProgress);

        String base64EncodedPublicKey = getResources().getString(R.string.public_key);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    queryPurchasedItems();
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
        CacheValues lData = new CacheValues(this);
        lData.saveStatus(isAdDisable);

        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.activity_main);
        TextView textViewProgress = (TextView) findViewById(R.id.textViewProgress);
        ScrollView pt = (ScrollView) findViewById(R.id.progress);

        textViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
        LinearLayout secondaryLayout = new LinearLayout(this);

        secondaryLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.topMargin = 450;


        secondaryLayout.setLayoutParams(layoutParams2);

        buyItem = new Button(this);
        buyItem.setText(R.string.buy_item);
        buyItem.setEnabled(false);
        buyItem.setLayoutParams(layoutParams);

        buyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(MainActivity.this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");
            }
        });

        enableBuy = new Button(this);
        enableBuy.setText(R.string.enable_buy);
        enableBuy.setLayoutParams(layoutParams);
        enableBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyItem.setEnabled(true);
                enableBuy.setEnabled(false);
            }
        });


        moveNext = new Button(this);
        moveNext.setText(R.string.move_next);
        moveNext.setLayoutParams(layoutParams);
        moveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InterstetailAd.class));
            }
        });


        input = new EditText(this);
        input.setLayoutParams(layoutParams);


        checkPalindrome = new Button(this);
        checkPalindrome.setText(R.string.move_next);
        checkPalindrome.setLayoutParams(layoutParams);
        checkPalindrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String longestPalindrome = checkPalindrome(input.getText().toString());
                input.setText(longestPalindrome);
            }
        });
        secondaryLayout.addView(enableBuy);
        secondaryLayout.addView(buyItem);
        secondaryLayout.addView(moveNext);


        rootLayout.addView(secondaryLayout);

    }

    public String checkPalindrome(String input) {
        String palindrome = "";
        int startOffSet = input.length();
        int endOffSet = 0;

        palindrome = getLongestPalindrome(startOffSet, endOffSet, input);

        return palindrome;
    }

    public String getLongestPalindrome(int startOffSet, int endOffSet, String input) {

        String reverse = "";
        for (int i = startOffSet - 1; i >= endOffSet; i--) {
            reverse += input.charAt(i);
        }

        if (input.contains(reverse)) {

            return reverse;
        } else {
            reverse = getLongestPalindrome(startOffSet - 1, endOffSet, input);

            if (input.contains(reverse)) {
                return reverse;
            } else {
                reverse = getLongestPalindrome(startOffSet, endOffSet + 1, input);

                if (input.contains(reverse)) {
                    return reverse;
                } else {
                    reverse = getLongestPalindrome(startOffSet - 1, endOffSet + 1, input);

                    if (input.contains(reverse)) {
                        return reverse;
                    } else {
                        return "no palindrome found";
                    }
                }
            }
        }


    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {

            progressText += "\n Info" + info + result;
            if (result.isFailure()) {
                progressText += "\n purchase failed at listener";
                progressTextView.setText(progressText);
                isAdDisable = false;
                return;
            } else if (info.getSku().equals(ITEM_SKU)) {
                progressText += "\n purchase successfull";
                progressTextView.setText(progressText);
                isAdDisable = true;
                CacheValues lData = new CacheValues(MainActivity.this);
                lData.saveStatus(isAdDisable);
                buyItem.setEnabled(false);
                queryPurchasedItems();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            progressText += "\n not an in app purchase result";
            progressTextView.setText(progressText);
            super.onActivityResult(requestCode, resultCode, data);
        } else {

            progressText += "\n in app request result" + resultCode + data;
            progressTextView.setText(progressText);
        }
//  When the purchasing process returns, it will call a method on the calling activity named onActivityResult,
// passing through as arguments the request code passed through to the launchPurchaseFlow method,
// a result code and intent data containing the purchase response.
//
//  This method needs to identify if it was called as a result of an in-app purchase request or some request
// unrelated to in-app billing. It does this by calling the handleActivityResult method of the mHelper instance
// and passing through the incoming arguments. If this is a purchase request the mHelper will handle it and return a true value.
// If this is not the result of a purchase, then the method needs to pass it up to the superclass to be handled.
//
    }

    private void queryPurchasedItems() {
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    //check if user has bought "remove adds"
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                isAdDisable = false;
                // handle error here
            } else {
                // does the user have the premium upgrade?
                isAdDisable = inventory.hasPurchase(ITEM_SKU);
                // update UI accordingly

            }
            showAd();
        }
    };


//    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
//        @Override
//        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
//
//            if (result.isFailure()) {
//                progressText += " \n mReceivedInventoryListener failed";
//                progressTextView.setText(progressText);
//
//            } else {
//                Purchase purchase = inv.getPurchase(ITEM_SKU);
//                if (purchase != null) {
//                    //purchased
//                }
//
//                progressText += "\n in consuming async" + ITEM_SKU + "purchase" + purchase;
//                //  mHelper.consumeAsync(inv.getPurchase(ITEM_SKU), mConsumeFinishedListener);
//                progressTextView.setText(progressText);
//            }
//        }
//    };


//    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
//        @Override
//        public void onConsumeFinished(Purchase purchase, IabResult result) {
//
//            if (result.isSuccess()) {
//                TextView tv = (TextView) findViewById(R.id.textViewProgress);
//                tv.setText("done purchase. Click to navigate to next screen");
//                tv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        startActivity(new Intent(MainActivity.this, SecondActivity.class));
//                    }
//                });
//                progressText += "\n success in mConsumeFinishedListener ";
//                enableBuy.setEnabled(true);
//                //  progressTextView.setText(progressText);
//            } else {
//                progressText += "error in mConsumeFinishedListener";
//                progressTextView.setText(progressText);
//            }
//        }
//    };


    private void consumeItem() {
        //  progressText += "\n in cosume method";
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    @Override
    protected void onDestroy() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;

        super.onDestroy();
    }
}
