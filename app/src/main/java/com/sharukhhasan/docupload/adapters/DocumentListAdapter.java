package com.sharukhhasan.docupload.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import com.sharukhhasan.docupload.activities.MainActivity;
import com.sharukhhasan.docupload.models.Document;
import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.activities.DocumentViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharukh on 2/21/16.
 */
public class DocumentListAdapter extends ArrayAdapter<Document> {
    List<Document> documentList = new ArrayList<Document>();
    Context context;
    LayoutInflater inflater;
    ParseFile docFile;
    String docImgURL;

    public DocumentListAdapter(Context context, ArrayList<Document> documentList)
    {
        super(context, R.layout.document_list, documentList);
        this.documentList = documentList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return documentList.size();
    }

    @Override
    public Document getItem(int position)
    {
        return documentList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        Document document = documentList.get(position);

        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.document_list, null);

            // get TextView in document_list layout
            holder.docName = (TextView) convertView.findViewById(R.id.document_name);

            // Add and download the image
            holder.docImg = (ImageView) convertView.findViewById(R.id.docImgView);
            //ParseFile imageFile = documentList.get(position).getParseFile("DocumentImage");
            docImgURL = documentList.get(position).getParseFile("DocumentImage").getUrl();
            //holder.docImg.setParseFile(imageFile);
            //holder.docImg.loadInBackground();

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.populate(document, context, ((MainActivity) context).isViewBusy());

        // listen for ListView component click
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, DocumentViewActivity.class);
                intent.putExtra("DocumentTitle", documentList.get(position).getTitle());
                intent.putExtra("DocumentType", documentList.get(position).getDocumentType());
                intent.putExtra("DocumentImageURL", documentList.get(position).getPhotoURL());
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    static class ViewHolder {
        TextView docName;
        ImageView docImg;

        void populate(Document doc, Context context)
        {
            docName.setText(doc.getTitle());

            Glide.with(context).load(doc.getPhotoURL()).into(docImg);
        }

        void populate(Document doc, Context context, boolean isBusy)
        {
            //docName.setText(doc.getTitle());

            if(!isBusy)
            {
                docName.setText(doc.getTitle());
                Glide.with(context).load(doc.getPhotoURL()).into(docImg);
            }
        }
    }
}
