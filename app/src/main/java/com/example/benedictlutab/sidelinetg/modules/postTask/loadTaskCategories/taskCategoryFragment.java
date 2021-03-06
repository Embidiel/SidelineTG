package com.example.benedictlutab.sidelinetg.modules.postTask.loadTaskCategories;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.swalDialogUtil;
import com.example.benedictlutab.sidelinetg.models.taskCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class taskCategoryFragment extends Fragment
{
    @BindView(R.id.rv_taskcategory) RecyclerView recyclerView;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvEmpty) TextView tvEmpty;
    @BindView(R.id.llShow) LinearLayout llShow;
    @BindView(R.id.llEmpty) LinearLayout llEmpty;

    private View rootView;
    private int listSize;
    private List<taskCategory> taskCategoryList = new ArrayList<>();

    public static taskCategoryFragment newInstance()
    {
        taskCategoryFragment taskCategoryFragment = new taskCategoryFragment();
        return taskCategoryFragment;
    }

    public taskCategoryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("onCreateView: ","STARTED!");

        rootView = inflater.inflate(R.layout.posttask_fragment_task_category, container, false);
        ButterKnife.bind(this, rootView);
        fetchTaskCategories();

        // Change Font Style.
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/avenir.otf");
        tvTitle.setTypeface(font, Typeface.BOLD);
        tvEmpty.setTypeface(font);

        return rootView;
    }

    private void initRecyclerView()
    {
        Log.e("initRecyclerView: ","STARTED!");

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        adapterTaskCategories adapterTaskCategories = new adapterTaskCategories(getActivity().getApplicationContext(), taskCategoryList);
        recyclerView.setAdapter(adapterTaskCategories);
        if (listSize == 0)
        {
            Log.e("initRecyclerView: ", "No task categories loaded!");
            llShow.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.e("initRecyclerView: ", "Task categories loaded!");
            llShow.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void fetchTaskCategories()
    {
        Log.e("fetchTaskCategories: ","STARTED!");

        apiRouteUtil apiRouteUtil = new apiRouteUtil();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRouteUtil.URL_LOAD_TASK_CATEGORIES, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    if(ServerResponse.replaceAll("\\s+","").equals("FAILED_CONN"))
                    {
                        Log.e("fetchTaskCategories: ", "FAILED_CONN");
                        swalDialogUtil swalDialogUtil = new swalDialogUtil(getActivity());
                        swalDialogUtil.showNetworkErrorDialog();

                        // Hide task categories layout
                        llShow.setVisibility(View.GONE);
                        llEmpty.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        JSONArray jsonArray = new JSONArray(ServerResponse);
                        for(int x = 0; x < jsonArray.length(); x++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            // Adding the jsonObject to the List.
                            taskCategoryList.add(new taskCategory(jsonObject.getString("task_category_id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("minimum_payment"),
                                    jsonObject.getString("task_category_img"))
                            );
                            listSize = taskCategoryList.size();
                            Log.e("listSize: ", String.valueOf(listSize));
                        }
                        initRecyclerView();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());

                    }
                });
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
    }
}


