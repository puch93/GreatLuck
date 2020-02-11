package kr.co.planet.newgreatluck.billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import kr.co.planet.newgreatluck.data.CharmData;


// git 테스트란다란다
public class BillingCharmManager implements PurchasesUpdatedListener {
    final String TAG = "TEST_INAPP";

    private BillingClient mBillingClient;
    private Activity act;

    public enum connectStatusTypes {wating, connected, fail, disconnected}

    public connectStatusTypes connectStatus = connectStatusTypes.wating;

    public List<SkuDetails> mSkuDetailsList;

    private ConsumeResponseListener mConsumeListener;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    public AfterBilling afterListener;

    String subscription_state;
    CharmData selectedItem;

    String itemId;


    public interface AfterBilling {
        void sendResult(Purchase purchase);

        void getSubsriptionState(String subscription, Purchase purchase);
    }


    public BillingCharmManager(Activity act, CharmData selectedItem, AfterBilling afterListener) {
        this.act = act;
        this.selectedItem = selectedItem;
        this.afterListener = afterListener;

        itemId = selectedItem.getId();

        Log.d(TAG, "구글 결제 매니저를 초기화 하고 있습니다.");

        mBillingClient = BillingClient.newBuilder(act).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connectStatus = connectStatusTypes.connected;
                    Log.d(TAG, "구글 결제 서버에 접속을 성공하였습니다.");

                    getSkuDetailList();

                    Purchase.PurchasesResult result = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    Log.i(TAG, "result: " + result);
                    Log.i(TAG, "getPurchasesList: " + result.getPurchasesList());
                    Log.i(TAG, "getResponseCode: " + result.getResponseCode());

//                    //이미 결제한 아이템인지 체크
//                    List<Purchase> purchasedList = result.getPurchasesList();
//                    if (purchasedList == null || purchasedList.size() == 0) {
//                        subscription_state = "N";
//                        afterListener.getSubsriptionState(subscription_state, null);
//                    } else {
//                        for (int i = 0; i < purchasedList.size(); i++) {
//                            if (purchasedList.get(i).getSku().equals(itemId)) {
//                                subscription_state = "Y";
//                                afterListener.getSubsriptionState(subscription_state, null);
//                                break;
//                            } else {
//                                if (i == purchasedList.size() - 1) {
//                                    subscription_state = "N";
//                                    afterListener.getSubsriptionState(subscription_state, null);
//                                }
//                            }
//                        }
//                    }

                    BillingResult result_billing = result.getBillingResult();
                    Log.i(TAG, "result_billing.getResponseCode: " + result_billing.getResponseCode());
                    Log.i(TAG, "result_billing.getDebugMessage: " + result_billing.getDebugMessage());

                    //오류
                } else {
                    connectStatus = connectStatusTypes.fail;
                    Log.d(TAG, "구글 결제 서버 접속에 실패하였습니다.\n오류코드: " + billingResult.getResponseCode());
                    subscription_state = "noAccount";
                    afterListener.getSubsriptionState(subscription_state, null);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectStatus = connectStatusTypes.disconnected;
                Log.d(TAG, "구글 결제 서버와 접속이 끊어졌습니다.");
            }
        });

        mConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "상품을 성공적으로 소모하였습니다. 소모된 상품 => " + purchaseToken);
                    return;
                } else {
                    Log.d(TAG, "상품 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + "), 대상 상품 코드: " + purchaseToken);
                    return;
                }
            }
        };

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "구독을 성공적으로 소모하였습니다.");
                    return;
                } else {
                    Log.d(TAG, "구독 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + ")");
                    return;
                }
            }
        };
    }

    //구입 가능한 상품의 리스트를 받아 오는 메소드
    public void getSkuDetailList() {
        //구글 상품 정보들의 ID를 만들어 줌
        List<String> Sku_ID_List = new ArrayList<>();
        Sku_ID_List.add(itemId);
//        Sku_ID_List.add("sub_charm");


        //1회성 소모품에 대한 SkuDetailsList 객체를 만듬
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        params.setSkusList(Sku_ID_List).setType(BillingClient.SkuType.INAPP);


        //비동기 상태로 앱의 정보를 가지고 옴
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                //상품 정보를 가지고 오지 못한 경우
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "상품 정보를 가지고 오던 중 오류가 발생했습니다.\n오류코드: " + billingResult.getResponseCode());
                    subscription_state = "error";
                    afterListener.getSubsriptionState(subscription_state, null);
                    return;
                }

                if (skuDetailsList == null) {
                    Log.d(TAG, "상품 정보가 존재하지 않습니다.");
                    return;
                }

                //응답 받은 데이터들의 숫자를 출력
                Log.d(TAG, "응답 받은 데이터 숫자: " + skuDetailsList.size());

                //받아온 상품 정보를 차례로 호출
                for (int sku_idx = 0; sku_idx < skuDetailsList.size(); sku_idx++) {
                    //해당 인덱스의 객체를 가지고 옴
                    SkuDetails _skuDetail = skuDetailsList.get(sku_idx);

                    //해당 인덱스의 상품 정보를 출력
                    Log.d(TAG, _skuDetail.getSku() + ": " + _skuDetail.getTitle() + ", " + _skuDetail.getPrice());
                    Log.d(TAG, _skuDetail.getOriginalJson());
                }

                //받은 값을 멤버 변수로 저장
                Log.e(TAG, "skuDetailsList: " + skuDetailsList);
                mSkuDetailsList = skuDetailsList;
            }
        });
    }


    //실제 구입 처리를 하는 메소드
    public void purchase(String item) {
        SkuDetails skuDetails = null;
        for (int i = 0; i < mSkuDetailsList.size(); i++) {
            SkuDetails details = mSkuDetailsList.get(i);
            if (details.getSku().equals(item)) {
                skuDetails = details;
                break;
            }
        }

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult billingResult = mBillingClient.launchBillingFlow(act, flowParams);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //결제에 성공한 경우
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "결제에 성공했으며, 아래에 구매한 상품들이 나열됨");

            for (Purchase _pur : purchases) {
                handlePurchase(_pur);
            }
        }

        //사용자가 결제를 취소한 경우
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "사용자에 의해 결제취소");
        }

        //그 외에 다른 결제 실패 이유
        else {
            Log.d(TAG, "결제가 취소 되었습니다. 종료코드: " + billingResult.getResponseCode());
        }
    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.


            //기본인앱상품
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            mBillingClient.consumeAsync(consumeParams, mConsumeListener);
            afterListener.sendResult(purchase);

//            // Acknowledge the purchase if it hasn't already been acknowledged.
//            if (!purchase.isAcknowledged()) {
//                AcknowledgePurchaseParams acknowledgePurchaseParams =
//                        AcknowledgePurchaseParams.newBuilder()
//                                .setPurchaseToken(purchase.getPurchaseToken())
//                                .build();
//                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
//
//
//                afterListener.sendResult(purchase);
//                afterListener.getSubsriptionState("Y", null);
//            }


        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
        }
    }

}
