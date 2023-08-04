package com.itsvks.editor.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itsvks.editor.Constants;
import com.itsvks.editor.databinding.LayoutTextInputBinding;
import com.itsvks.editor.managers.DrawableManager;

import com.itsvks.editor.managers.ProjectManager;
import com.itsvks.editor.managers.ValuesManager;
import com.itsvks.editor.parser.ValuesResourceParser;
import java.util.regex.Pattern;

public class StringDialog extends AttributeDialog {

  /** Binding object for textInputLayout layout */
  private LayoutTextInputBinding binding;

  /** TextInputLayout object */
  private TextInputLayout textInputLayout;

  /** TextInputEditText object */
  private TextInputEditText textInputEditText;

  /** Boolean flag to check if the dialog is for drawable */
  String argumentType;

  /**
   * Constructor for StringDialog class
   *
   * @param context     The Activity context
   * @param savedValue  The saved value
   * @param isDrawable  Boolean flag to check for drawable
   */
  public StringDialog(Context context, String savedValue, String argumentType) {
    super(context);
    this.argumentType = argumentType;
    binding = LayoutTextInputBinding.inflate(getDialog().getLayoutInflater());

    textInputLayout = binding.getRoot();
    textInputLayout.setHint("Enter string value");

    textInputEditText = binding.textInputEdittext;
    textInputEditText.setText(savedValue);

    switch (argumentType) {
      case Constants.ARGUMENT_TYPE_DRAWABLE:
        textInputLayout.setHint("Enter drawable name");
        textInputLayout.setPrefixText("@drawable/");
        textInputEditText.addTextChangedListener(
            new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

              @Override
              public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

              @Override
              public void afterTextChanged(Editable arg0) {
                checkErrors();
              }
            });
        break;
      case Constants.ARGUMENT_TYPE_TEXT:
        textInputLayout.setHint("Enter string value");
        break;
      case Constants.ARGUMENT_TYPE_STRING:
        textInputLayout.setHint("Enter string name");
        textInputLayout.setPrefixText("@string/");
        textInputEditText.addTextChangedListener(
            new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

              @Override
              public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

              @Override
              public void afterTextChanged(Editable arg0) {
                checkErrors();
              }
            });
        break;
    }

    setView(textInputLayout, 10);
    showKeyboardWhenOpen();
  }

  /** Method to check for errors */
  private void checkErrors() {
    String text = textInputEditText.getText().toString();
    if (!argumentType.equals(Constants.ARGUMENT_TYPE_TEXT)) {

      if (text.equals("")) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("Field cannot be empty!");
        setEnabled(false);
        return;
      }

      if (!Pattern.matches("[a-z_][a-z0-9_]*", text)) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("Only small letters(a-z) and numbers!");
        setEnabled(false);
        return;
      }

      if (argumentType.equals(Constants.ARGUMENT_TYPE_DRAWABLE)
          && !DrawableManager.contains(text)) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("No Drawable found");
        setEnabled(false);
        return;
      }

      if (argumentType.equals(Constants.ARGUMENT_TYPE_STRING)
          && ValuesManager.getValueFromResources(
                  ValuesResourceParser.TAG_STRING,
                  (text.startsWith("@string/") ? text : "@string/" + text),
                  ProjectManager.getInstance().getOpenedProject().getStringsPath())
              == null) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("No string found");
        setEnabled(false);
        return;
      }
    }

    textInputLayout.setErrorEnabled(false);
    textInputLayout.setError("");
    setEnabled(true);
  }

  /** Method to show the dialog */
  @Override
  public void show() {
    super.show();
    requestEditText(textInputEditText);
    checkErrors();
  }

  /** Method to be invoked when the save button is clicked */
  @Override
  protected void onClickSave() {
    super.onClickSave();
    String text = textInputEditText.getText().toString();
    switch (argumentType) {
      case Constants.ARGUMENT_TYPE_DRAWABLE:
        listener.onSave("@drawable/" + text);
        break;
      case Constants.ARGUMENT_TYPE_STRING:
        listener.onSave("@string/" + text);
        break;
      case Constants.ARGUMENT_TYPE_TEXT:
        listener.onSave(text);
        break;
    }
  }
}