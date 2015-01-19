package de.devland.lockscreenbirthdays;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.lockscreenbirthdays.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by David Kunzler on 17.01.2015.
 */
@AllArgsConstructor(suppressConstructorProperties = true)
public class BirthdayAdapter extends RecyclerView.Adapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.image_contact)
        public ImageView contactImage;
        @InjectView(R.id.textView_message)
        public TextView message;
        @InjectView(R.id.textView_name)
        public TextView name;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.inject(this, itemView);
        }
    }

    private Context context;

    @Getter
    @Setter
    protected List<Contact> birthdays;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Contact contact = birthdays.get(position);
        ViewHolder vh = (ViewHolder) viewHolder;
        String photoUri = contact.getPhotoUri();
        if (photoUri != null) {
            vh.contactImage.setImageURI(Uri.parse(photoUri));
        } else {
            vh.contactImage.setImageResource(R.drawable.ic_account_default);
        }
        vh.name.setText(contact.getDisplayName());
        vh.message.setText(contact.getMessageText(context));
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId()));
                contactIntent.setData(uri);
                context.startActivity(contactIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return birthdays.size();
    }


}
