package de.devland.lockscreenbirthdays.prefs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.App;
import de.devland.lockscreenbirthdays.R;
import de.devland.lockscreenbirthdays.util.Icon;

public class IconPickerPreference extends ListPreference {

    private class CustomListPreferenceAdapter extends ArrayAdapter<IconItem> {

        private Context context;
        private List<IconItem> icons;
        private int resource;

        public CustomListPreferenceAdapter(Context context, int resource,
                                           List<IconItem> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.icons = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);

                holder = new ViewHolder();
                holder.iconName = (TextView) convertView
                        .findViewById(R.id.iconName);
                holder.iconImage = (ImageView) convertView
                        .findViewById(R.id.iconImage);
                holder.radioButton = (RadioButton) convertView
                        .findViewById(R.id.iconRadio);
                holder.position = position;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iconName.setText(icons.get(position).name);

            Icon currentIcon = Icon.valueOf(icons.get(position).file);
            holder.iconImage.setImageResource(currentIcon.getIconId());

            holder.radioButton.setChecked(icons.get(position).isChecked);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    for (int i = 0; i < icons.size(); i++) {
                        icons.get(i).isChecked = i == holder.position;
                    }
                    getDialog().dismiss();
                }
            });

            return convertView;
        }

    }

    private static class IconItem {

        private String file;
        private boolean isChecked;
        private String name;

        public IconItem(CharSequence name, CharSequence file, boolean isChecked) {
            this(name.toString(), file.toString(), isChecked);
        }

        public IconItem(String name, String file, boolean isChecked) {
            this.name = name;
            this.file = file;
            this.isChecked = isChecked;
        }

    }

    private static class ViewHolder {
        protected ImageView iconImage;
        protected TextView iconName;
        protected int position;
        protected RadioButton radioButton;
    }

    private Context context;
    private ImageView iconImage;

    private CharSequence[] iconFile;
    private CharSequence[] iconName;
    private List<IconItem> icons;
    private Resources resources;
    private String selectedIconFile;

    public IconPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        resources = context.getResources();
    }

    private String getEntry(String value) {
        String[] entries = resources.getStringArray(R.array.iconNames);
        String[] values = resources.getStringArray(R.array.iconEntries);
        int index = Arrays.asList(values).indexOf(value);
        return entries[index];
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        selectedIconFile = defaultPrefs.icon();

        iconImage = (ImageView) view.findViewById(R.id.iconSelected);
        updateIcon();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (icons != null) {
            for (int i = 0; i < iconName.length; i++) {
                IconItem item = icons.get(i);
                if (item.isChecked) {

                    DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, App.getInstance());
                    defaultPrefs.icon(item.file);

                    selectedIconFile = item.file;
                    updateIcon();

                    break;
                }
            }
        }

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton(null, null);

        iconName = getEntries();
        iconFile = getEntryValues();

        if (iconName == null || iconFile == null
                || iconName.length != iconFile.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array "
                            + "and an entryValues array which are both the same length");
        }

        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        String selectedIcon = defaultPrefs.icon();

        icons = new ArrayList<IconItem>();

        for (int i = 0; i < iconName.length; i++) {
            boolean isSelected = selectedIcon.equals(iconFile[i]) ? true
                    : false;
            IconItem item = new IconItem(iconName[i], iconFile[i], isSelected);
            icons.add(item);
        }

        CustomListPreferenceAdapter customListPreferenceAdapter = new CustomListPreferenceAdapter(
                context, R.layout.preference_icon_picker, icons);
        builder.setAdapter(customListPreferenceAdapter, null);

    }

    private void updateIcon() {
        Icon icon = Icon.valueOf(selectedIconFile.toString());
        icon.set();
        iconImage.setImageResource(icon.getIconId());
    }

}