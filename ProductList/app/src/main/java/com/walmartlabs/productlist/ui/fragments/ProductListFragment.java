package com.walmartlabs.productlist.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.walmartlabs.productlist.R;
import com.walmartlabs.productlist.adapters.ProductAdapter;
import com.walmartlabs.productlist.bean.ProductBean;
import com.walmartlabs.productlist.controller.ProductController;
import com.walmartlabs.productlist.util.LoadMoreListView;

import java.util.List;

public class ProductListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnProductListActionListener mProductListActionListener;
    private ProductAdapter mProductAdapter;
    private LoadMoreListView mLoadMoreListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProductAdapter = new ProductAdapter(getActivity(), null);
        getLoaderManager().initLoader(0, null, this);

        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLoadMoreListView = (LoadMoreListView) view.findViewById(R.id.listview_product);
        mLoadMoreListView.setAdapter(mProductAdapter);

        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int previousTotal) {
                ProductController productController = new ProductController(getActivity());
                productController.loadProducts(true);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mProductListActionListener = (OnProductListActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProductListActionListener = null;
    }

    //Loader methods
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        ProductController productController = new ProductController(getActivity().getApplicationContext());
        return productController.getProductLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mProductAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mProductAdapter.swapCursor(null);
    }

    public void loadCompleted() {
        if (mLoadMoreListView != null) {
            mLoadMoreListView.loadMoreCompleted();
        }
    }

    public interface OnProductListActionListener {
        public void onClickProduct(ProductBean productBean);
    }

}
