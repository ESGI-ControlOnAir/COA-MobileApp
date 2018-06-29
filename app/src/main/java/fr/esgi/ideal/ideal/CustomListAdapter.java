package fr.esgi.ideal.ideal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.esgi.ideal.ideal.api.Article;

public class CustomListAdapter extends BaseAdapter {
    private List<Article> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Article> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.articleliste, null);
            holder = new ViewHolder();
            holder.flagView = (ImageView) convertView.findViewById(R.id.article_picture);
            holder.titre = (TextView) convertView.findViewById(R.id.article_titre);
            holder.content = (TextView) convertView.findViewById(R.id.article_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article art = this.listData.get(position);
        holder.titre.setText(art.getName());
        holder.content.setText("Population: " +art.getName());

        //int imageId = this.getMipmapResIdByName("nopic");

        holder.flagView.setImageResource(R.mipmap.nopic);

        return convertView;
    }

    // Find Image ID corresponding to the name of the image (in the directory mipmap).
    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName , "mipmap", pkgName);
        Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    static class ViewHolder {
        ImageView flagView;
        TextView titre;
        TextView content;
    }
}
