package com.sam_chordas.android.stockhawk;

import com.robinhood.spark.SparkAdapter;

/**
 * Created by aki on 21-Jul-16.
 */

public class GraphAdapter  extends SparkAdapter {
        private float[] yData;

        public GraphAdapter(float[] yData) {
            this.yData = yData;
        }

        @Override
        public int getCount() {
            return yData.length;
        }

        @Override
        public Object getItem(int index) {
            return yData[index];
        }

        @Override
        public float getY(int index) {
            return yData[index];
        }
    }
