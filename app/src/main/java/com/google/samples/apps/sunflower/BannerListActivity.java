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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BannerListActivity extends AppCompatActivity {

    private static final String PLACEMENT_ID = "placement_id";
    private static final Integer SIZE = 40;
    private static final int POSITION = 20;

    private RecyclerView recyclerView;
    private VungleBannerAdAdapter adapter;

    public static Intent getIntent(Context ctx, String placementReferenceId) {
        Intent intent = new Intent(ctx, BannerListActivity.class);
        intent.putExtra(PLACEMENT_ID, placementReferenceId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);
        String placement = getIntent().getStringExtra(PLACEMENT_ID);

        recyclerView = findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        RVAdapter originalAdapter = new RVAdapter(SIZE);
        adapter = new VungleBannerAdAdapter(placement, POSITION, originalAdapter, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        adapter.destroy();
        recyclerView.setAdapter(null);
        super.onDestroy();
    }

    //simple adapter implementation
    private static class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Integer size;

        RVAdapter(Integer size) {
            this.size = size;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(type, viewGroup, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ((ItemHolder) viewHolder).bind(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return size;
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.rv_item;
        }
    }
}