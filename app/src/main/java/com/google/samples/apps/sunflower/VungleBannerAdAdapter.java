/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

public class VungleBannerAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BANNED_AD_TYPE = R.layout.ad_item;
    private final String placementId;
    private final int adPosition;
    private final RecyclerView.Adapter originalAdapter;
    private final PlayAdCallback playAdCallback;
    private VungleBanner ad;
    private boolean destroyed;

    public VungleBannerAdAdapter(@NonNull String placementId,
                                 int adPosition,
                                 @NonNull RecyclerView.Adapter originalAdapter,
                                 @Nullable PlayAdCallback playAdCallback) {
        this.placementId = placementId;
        this.adPosition = adPosition;
        this.originalAdapter = originalAdapter;
        this.playAdCallback = playAdCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if (type == BANNED_AD_TYPE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(type, viewGroup, false);
            return new OneAdHolder(view);
        }
        return originalAdapter.onCreateViewHolder(viewGroup, type);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == BANNED_AD_TYPE) {
            ((OneAdHolder) holder).bind(placementId);
        } else {
            //noinspection unchecked
            originalAdapter.onBindViewHolder(holder, position < adPosition ? position : position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + originalAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == adPosition) {
            return BANNED_AD_TYPE;
        } else {
            return position < adPosition ?
                    originalAdapter.getItemViewType(position) :
                    originalAdapter.getItemViewType(position + 1);
        }
    }

    //must be called
    public void destroy() {
        destroyed = true;
        if (ad != null) {
            ad.destroyAd();
        }
    }

    private boolean canStart() {
        return ad == null && !destroyed;
    }

    private class OneAdHolder extends RecyclerView.ViewHolder {
        private final ViewGroup viewGroup;

        OneAdHolder(@NonNull View itemView) {
            super(itemView);
            viewGroup = itemView.findViewById(R.id.ad_container);
        }

        void bind(final String placement) {
            if (canStart()) {
                final AdConfig.AdSize size = AdConfig.AdSize.BANNER;
                Banners.loadBanner(placement, size, new LoadAdCallback() {
                    @Override
                    public void onAdLoad(String s) {
                        if (canStart()) {
                            ad = Banners.getBanner(placement, size, playAdCallback);
                            if (ad != null) {
                                ad.disableLifeCycleManagement(true);
                                viewGroup.addView(ad);
                                ad.renderAd();
                            }
                        }
                    }

                    @Override
                    public void onError(String s, VungleException e) {
                    }
                });
            }
        }
    }
}