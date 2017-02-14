package it.nicolabrogelli.imedici.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import it.nicolabrogelli.imedici.R;
import it.nicolabrogelli.imedici.interfaces.OnTapListener;
import it.nicolabrogelli.imedici.utils.ImageLoaderFromDrawable;
import it.nicolabrogelli.imedici.utils.MySingleton;

/**
 * Created by Nicola on 08/06/2016.
 */
public class AdapterItineraries extends RecyclerView.Adapter<AdapterItineraries.ViewHolder>  {

    // Create arraylist variables to store data
    private ArrayList<String> mItineraryIds;
    private ArrayList<String> mItineraryNames;
    private ArrayList<String> mItineraryImages;
    private ArrayList<String> mItineraryDescription;
    private ArrayList<String> mItineraryDistances;


    // Create listener, mContext and imageloader objects,
    // and also variables to store image width and height sizes
    private OnTapListener onTapListener;
    private Context mContext;
    private ImageLoaderFromDrawable mImageLoaderFromDrawable;
    private final ImageLoader IMAGE_LOADER;

    public AdapterItineraries(Context context)
    {
        this.mItineraryIds = new ArrayList<>();
        this.mItineraryNames = new ArrayList<>();
        this.mItineraryImages = new ArrayList<>();
        this.mItineraryDescription = new ArrayList<>();
        this.mItineraryDistances = new ArrayList<>();

        mContext = context;

        // Get image width and height sizes
        int mImageWidth = mContext.getResources().getDimensionPixelSize(R.dimen.thumb_width);
        int mImageHeight = mContext.getResources().getDimensionPixelSize(R.dimen.thumb_height);

        mImageLoaderFromDrawable = new ImageLoaderFromDrawable(mContext, mImageWidth, mImageHeight);
        IMAGE_LOADER = MySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        // Set item layout
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_locations, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)
    {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTapListener != null)
                    onTapListener.onTapView(position);
            }
        });

        // Set data to view
        viewHolder.mTxtLocationName.setText(mItineraryNames.get(position));
        viewHolder.mTxtLocationAddress.setText(mItineraryDescription.get(position));

        String mFinalDistance = String.valueOf(mItineraryDistances.get(position)) + " " +
                mContext.getResources().getString(R.string.km);
        viewHolder.mTxtLocationDistance.setText(mFinalDistance);

        // Set different background color for even and odd item
        if((position % 2) == 0) {
            viewHolder.mLytAdapter.setBackgroundColor(mContext.getResources().
                    getColor(R.color.material_background_color_2));
        }else{
            viewHolder.mLytAdapter.setBackgroundColor(mContext.getResources().
                    getColor(R.color.material_background_color));
        }

        if(mItineraryImages.get(position).toLowerCase().contains("http")){
            IMAGE_LOADER.get(mItineraryImages.get(position),
                    com.android.volley.toolbox.ImageLoader.
                            getImageListener(viewHolder.mImgLocationImage,
                                    R.mipmap.empty_photo, R.mipmap.empty_photo));

        } else {
            int image = mContext.getResources().getIdentifier(mItineraryImages.get(position),
                    "drawable", mContext.getPackageName());

            // Load image lazily
            mImageLoaderFromDrawable.loadBitmap(image, viewHolder.mImgLocationImage);
        }
    }

    @Override
    public int getItemCount()
    {
        return mItineraryIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // Create view objects
        private TextView mTxtLocationName, mTxtLocationAddress, mTxtLocationDistance;
        private RoundedImageView mImgLocationImage;
        private RelativeLayout mLytAdapter;

        public ViewHolder(View v)
        {
            super(v);

            // Connect view objects with view ids in xml
            mTxtLocationName = (TextView) v.findViewById(R.id.txtLocationName);
            mImgLocationImage = (RoundedImageView) v.findViewById(R.id.imgLocationImage);
            mTxtLocationAddress = (TextView) v.findViewById(R.id.txtLocationAddress);
            mTxtLocationDistance = (TextView) v.findViewById(R.id.txtLocationDistance);
            mLytAdapter = (RelativeLayout) v.findViewById(R.id.lytAdapter);
        }
    }

    // Method to update data
    public void updateList(ArrayList<String> itineraryIds,
                           ArrayList<String> itineraryNames,
                           ArrayList<String> itineraryImages,
                           ArrayList<String> itineraryDescription,
                           ArrayList<String> itineraryDistances) {

        this.mItineraryIds.clear();
        this.mItineraryIds.addAll(itineraryIds);

        this.mItineraryNames.clear();
        this.mItineraryNames.addAll(itineraryNames);

        this.mItineraryImages.clear();
        this.mItineraryImages.addAll(itineraryImages);

        this.mItineraryDescription.clear();
        this.mItineraryDescription.addAll(itineraryDescription);

        this.mItineraryDistances.clear();
        this.mItineraryDistances.addAll(itineraryDistances);

        this.notifyDataSetChanged();
    }

    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }
}