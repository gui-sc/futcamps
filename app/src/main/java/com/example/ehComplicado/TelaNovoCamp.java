package com.example.ehComplicado;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import model.bean.Campeonato;


public class TelaNovoCamp extends BaseActivity {

    private TextInputLayout nomeTextInput;
    private TextInputEditText nomeEditText;
    private TextInputLayout cidadeTextInput;
    private TextInputEditText cidadeEditText;
    private TextInputEditText premiacaoEditText;
    private TextInputLayout premiacaoTextInput;
    private TextInputLayout classificadosTextInput;
    private TextInputEditText classificadosEditText;
    private TextInputLayout penduradosTextInput;
    private TextInputEditText penduradosEditText;
    private Spinner spGrupos;
    private String nome, cidade, premiacao, formato;
    private boolean faseDeGrupos;
    private int numTimes, numGrupos, ano, classificados, cartoesPendurado;
    FirebaseUser user;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.ftc_novo_camp);
        final RadioButton rdOitavas = findViewById(R.id.rdOitavas);
        final RadioButton rdQuartas = findViewById(R.id.rdQuartas);
        final RadioButton rdSemi = findViewById(R.id.rdSemi);
        final TextView lblZerar = findViewById(R.id.lbl_zerar);
        final RadioButton rdSim = findViewById(R.id.rdSim);
        final RadioButton rdNao = findViewById(R.id.rdNao);
        final Button btnAnt = findViewById(R.id.btn_ant);
        final Button btnProx = findViewById(R.id.btn_prox);
        final TextView lblPergPremiacao = findViewById(R.id.lbl_perg_premiacao);
        final TextView lblPergPendurado = findViewById(R.id.lbl_pendurado);
        nomeTextInput = findViewById(R.id.nome_text_input);
        nomeEditText = findViewById(R.id.nome_edit_text);
        cidadeTextInput = findViewById(R.id.cidade_text_input);
        cidadeEditText = findViewById(R.id.cidade_edit_text);
        premiacaoEditText = findViewById(R.id.premiacao_edit_text);
        premiacaoTextInput = findViewById(R.id.premiacao_text_input);
        classificadosTextInput = findViewById(R.id.classificados_text_input);
        classificadosEditText = findViewById(R.id.classificados_edit_text);
        penduradosTextInput = findViewById(R.id.pendurado_text_input);
        penduradosEditText = findViewById(R.id.pendurado_edit_text);

        progressBar = findViewById(R.id.progressBar);

        user = getIntent().getParcelableExtra("user");

        numTimes = 0;
        numGrupos = 0;
        ano = 0;
        classificados = 0;
        cartoesPendurado = 0;
        rdSim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdNao.setChecked(false);
                    premiacaoTextInput.setVisibility(View.VISIBLE);
                    premiacaoEditText.setText("");
                }
            }
        });
        rdNao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdSim.setChecked(false);
                    premiacaoTextInput.setVisibility(View.INVISIBLE);
                    premiacaoEditText.setText(R.string.ftc_sem_premiacao);
                }
            }
        });
        spGrupos = findViewById(R.id.spGrupos);
        spGrupos.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        spGrupos.setClickable(false);
        final ArrayAdapter<String> seisTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.seisTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        final ArrayAdapter<String> oitoTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.oitoTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        final ArrayAdapter<String> dezTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.dezTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        final ArrayAdapter<String> dozeTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.dozeTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        final ArrayAdapter<String> dezesseisTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.dezesseisTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        final Spinner spFormato = findViewById(R.id.spFormato);
        spFormato.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spFormatoAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.formato)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        spFormatoAdapter.setDropDownViewResource(R.layout.personalizado_spinner_dropdown_item);
        spFormato.setAdapter(spFormatoAdapter);
        spFormato.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formato = String.valueOf(parent.getSelectedItem());
                if (parent.getSelectedItemPosition() == 1) {
                    spGrupos.setEnabled(true);
                    faseDeGrupos = true;
                    if (numTimes == 6) {
                        spGrupos.setAdapter(seisTimesAdapter);
                    } else if (numTimes == 8) {
                        spGrupos.setAdapter(oitoTimesAdapter);
                    } else if (numTimes == 10) {
                        spGrupos.setAdapter(dezTimesAdapter);
                    } else if (numTimes == 12) {
                        spGrupos.setAdapter(dozeTimesAdapter);
                    } else if (numTimes == 16) {
                        spGrupos.setAdapter(dezesseisTimesAdapter);
                    }
                    rdSemi.setVisibility(View.INVISIBLE);
                    rdQuartas.setVisibility(View.INVISIBLE);
                    rdOitavas.setVisibility(View.INVISIBLE);
                    rdSemi.setClickable(false);
                    rdQuartas.setClickable(false);
                    rdOitavas.setClickable(false);
                    if (nomeTextInput.getVisibility() == View.INVISIBLE) {
                        spGrupos.setVisibility(View.VISIBLE);
                        classificadosTextInput.setVisibility(View.VISIBLE);
                        classificadosEditText.setText("");
                    }
                } else {
                    spGrupos.setEnabled(false);
                    faseDeGrupos = false;
                    spGrupos.setVisibility(View.INVISIBLE);
                    classificadosTextInput.setVisibility(View.INVISIBLE);
                    classificadosEditText.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner spNumTimes = findViewById(R.id.spNumTimes);
        spNumTimes.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spNumTimesAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, getResources().getStringArray(R.array.numTimes)) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        spNumTimesAdapter.setDropDownViewResource(R.layout.personalizado_spinner_dropdown_item);
        spNumTimes.setAdapter(spNumTimesAdapter);
        spNumTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (faseDeGrupos) {
                    if (position == 1) {
                        spGrupos.setAdapter(seisTimesAdapter);
                        numTimes = 6;
                    } else if (position == 2) {
                        spGrupos.setAdapter(oitoTimesAdapter);
                        numTimes = 8;
                    } else if (position == 3) {
                        spGrupos.setAdapter(dezTimesAdapter);
                        numTimes = 10;
                    } else if (position == 4) {
                        spGrupos.setAdapter(dozeTimesAdapter);
                        numTimes = 12;
                    } else if (position == 5) {
                        spGrupos.setAdapter(dezesseisTimesAdapter);
                        numTimes = 16;
                    }
                } else {
                    if (position == 1) {
                        numTimes = 6;
                    } else if (position == 2) {
                        numTimes = 8;
                    } else if (position == 3) {
                        numTimes = 10;
                    } else if (position == 4) {
                        numTimes = 12;
                    } else if (position == 5) {
                        numTimes = 16;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    numGrupos = 2;
                } else if (position == 2) {
                    numGrupos = 4;
                }
                if ((numGrupos * classificados) == 4) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdQuartas.setVisibility(View.INVISIBLE);
                    rdOitavas.setVisibility(View.INVISIBLE);
                    rdSemi.setClickable(true);
                    rdQuartas.setClickable(false);
                    rdOitavas.setClickable(false);
                } else if ((numGrupos * classificados) == 6 || (numGrupos * classificados) == 8) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdQuartas.setVisibility(View.VISIBLE);
                    rdOitavas.setVisibility(View.INVISIBLE);
                    rdSemi.setClickable(true);
                    rdQuartas.setClickable(true);
                    rdOitavas.setClickable(false);
                } else if ((numGrupos * classificados == 12 || (numGrupos * classificados) == 16)) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdQuartas.setVisibility(View.VISIBLE);
                    rdOitavas.setVisibility(View.VISIBLE);
                    rdSemi.setClickable(true);
                    rdQuartas.setClickable(true);
                    rdOitavas.setClickable(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner spAno = findViewById(R.id.spAno);
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> anos = new ArrayList<>();
        anos.add(getString(R.string.ftc_ano));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR)));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR) + 1));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR) + 2));
        spAno.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spAnoAdapter =
                new ArrayAdapter<String>(this, R.layout.personalizado_list_item, anos) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                            tv.setBackgroundColor(Color.WHITE);

                        } else {
                            tv.setTextColor(Color.BLACK);
                            tv.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
        spAnoAdapter.setDropDownViewResource(R.layout.personalizado_spinner_dropdown_item);
        spAno.setAdapter(spAnoAdapter);
        spAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(parent.getSelectedItem());
                if (position != 0) {
                    ano = Integer.valueOf(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Campeonato camp = getIntent().getParcelableExtra("camp");
        if (camp != null) {
            nomeEditText.setText(camp.getNome());
            cidadeEditText.setText(camp.getCidade());
            premiacaoEditText.setText(camp.getPremiacao());
            if (!camp.getPremiacao().equals(getString(R.string.ftc_sem_premiacao))) {
                rdSim.setChecked(true);
            } else {
                rdNao.setChecked(true);
            }
            spFormato.setSelection(spFormatoAdapter.getPosition(camp.getFormato()));
            spAno.setSelection(spAnoAdapter.getPosition(String.valueOf(camp.getAno())));
            spNumTimes.setSelection(spNumTimesAdapter.getPosition(String.valueOf(camp.getNumTimes())));
            if (camp.getNumGrupos() == 2) {
                if (camp.getNumTimes() == 16) {
                    spGrupos.setSelection(dezesseisTimesAdapter.getPosition(getString(R.string.doisgrupos)));
                } else if (camp.getNumTimes() == 12) {
                    spGrupos.setSelection(dozeTimesAdapter.getPosition(getString(R.string.doisgrupos)));
                } else if (camp.getNumTimes() == 10) {
                    spGrupos.setSelection(dezTimesAdapter.getPosition(getString(R.string.doisgrupos)));
                } else if (camp.getNumTimes() == 8) {
                    spGrupos.setSelection(oitoTimesAdapter.getPosition(getString(R.string.doisgrupos)));
                } else if (camp.getNumTimes() == 6) {
                    spGrupos.setSelection(seisTimesAdapter.getPosition(getString(R.string.doisgrupos)));
                }
            } else {
                if (camp.getNumTimes() == 16) {
                    spGrupos.setSelection(dezesseisTimesAdapter.getPosition(getString(R.string.quatrogrupos)));
                } else if (camp.getNumTimes() == 12) {
                    spGrupos.setSelection(dozeTimesAdapter.getPosition(getString(R.string.quatrogrupos)));
                } else if (camp.getNumTimes() == 10) {
                    spGrupos.setSelection(dezTimesAdapter.getPosition(getString(R.string.quatrogrupos)));
                } else if (camp.getNumTimes() == 8) {
                    spGrupos.setSelection(oitoTimesAdapter.getPosition(getString(R.string.quatrogrupos)));
                } else if (camp.getNumTimes() == 6) {
                    spGrupos.setSelection(seisTimesAdapter.getPosition(getString(R.string.quatrogrupos)));
                }
            }
            classificadosEditText.setText(String.valueOf(camp.getClassificados()));
        }
        btnProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = String.valueOf(nomeEditText.getText());
                cidade = String.valueOf(cidadeEditText.getText());
                premiacao = String.valueOf(premiacaoEditText.getText());
                if (btnProx.getText().equals(getString(R.string.ftc_prox_abrev))) {
                    if (!valida(nome)) {
                        nomeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        nomeTextInput.setError(null);
                        if (!valida(cidade)) {
                            cidadeTextInput.setError(getString(R.string.ftc_aviso_vazio));
                        } else {
                            cidadeTextInput.setError(null);
                            if (rdSim.isChecked() && !valida(premiacao)) {
                                premiacaoTextInput.setError(getString(R.string.ftc_aviso_vazio));
                            } else if (ano == 0) {
                                Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_ano, Toast.LENGTH_LONG).show();
                            } else {
                                premiacaoTextInput.setError(null);
                                nomeTextInput.setVisibility(View.INVISIBLE);
                                cidadeTextInput.setVisibility(View.INVISIBLE);
                                premiacaoTextInput.setVisibility(View.INVISIBLE);
                                lblPergPremiacao.setVisibility(View.INVISIBLE);
                                rdNao.setVisibility(View.INVISIBLE);
                                rdSim.setVisibility(View.INVISIBLE);
                                spAno.setVisibility(View.INVISIBLE);
                                nomeEditText.setClickable(false);
                                cidadeEditText.setClickable(false);
                                premiacaoEditText.setClickable(false);
                                rdNao.setClickable(false);
                                rdSim.setClickable(false);
                                spAno.setClickable(false);
                                btnAnt.setVisibility(View.VISIBLE);
                                btnAnt.setClickable(true);
                                btnProx.setText(R.string.ftc_revisar);
                                spFormato.setVisibility(View.VISIBLE);
                                spNumTimes.setVisibility(View.VISIBLE);
                                lblZerar.setVisibility(View.VISIBLE);
                                lblPergPendurado.setVisibility(View.VISIBLE);
                                penduradosTextInput.setVisibility(View.VISIBLE);
                                spFormato.setClickable(true);
                                spNumTimes.setClickable(true);
                                if (camp != null) {
                                    if (camp.getFormato().equals(getString(R.string.fasedegrupos))) {
                                        spGrupos.setVisibility(View.VISIBLE);
                                        classificadosTextInput.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressBar.setProgress(50, true);
                                } else {
                                    progressBar.setProgress(50);
                                }
                            }
                        }
                    }
                } else {
                    classificados = Integer.parseInt(classificadosEditText.getText().toString());
                    cartoesPendurado = Integer.parseInt(penduradosEditText.getText().toString());
                    if (formato.isEmpty()) {
                        Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_formato, Toast.LENGTH_LONG).show();
                    } else if (numTimes == 0) {
                        Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_num_times, Toast.LENGTH_LONG).show();
                    } else if (spGrupos.getVisibility() == View.VISIBLE && spGrupos.getSelectedItemPosition() == 0) {
                        Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_num_grupos, Toast.LENGTH_LONG).show();
                    } else if (formato.equals(getString(R.string.fasedegrupos)) && classificados == 0) {
                        Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_classificados, Toast.LENGTH_SHORT).show();
                    } else if (spGrupos.getVisibility() == View.VISIBLE && (numTimes / numGrupos) < classificados) {
                        Toast.makeText(TelaNovoCamp.this, R.string.ftc_aviso_classificados2, Toast.LENGTH_SHORT).show();
                    } else if (cartoesPendurado == 0) {
                        penduradosTextInput.setError(getString(R.string.ftc_aviso_vazio));
                    } else {
                        penduradosTextInput.setError(null);
                        Campeonato camp = new Campeonato();
                        camp.setNome(nome);
                        camp.setCidade(cidade);
                        camp.setPremiacao(premiacao);
                        camp.setFormato(formato);
                        camp.setNumTimes(numTimes);
                        camp.setNumGrupos(numGrupos);
                        camp.setAno(ano);
                        camp.setClassificados(classificados);
                        camp.setZerarCartoesOitavas(rdOitavas.isChecked());
                        camp.setZerarCartoesQuartas(rdQuartas.isChecked());
                        camp.setZerarCartoesSemi(rdSemi.isChecked());
                        camp.setCartoesPendurado(cartoesPendurado - 1);
                        Intent it = new Intent(TelaNovoCamp.this, TelaRevisao.class);
                        it.putExtra("user", user);
                        it.putExtra("camp", camp);
                        startActivity(it);
                        finish();
                    }
                }
            }
        });
        classificadosEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!classificadosEditText.getText().toString().equals("")) {
                    classificados = Integer.valueOf(String.valueOf(classificadosEditText.getText()));
                }
                if ((numGrupos * classificados) == 4) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdSemi.setClickable(true);
                } else if ((numGrupos * classificados) == 6 || (numGrupos * classificados) == 8) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdQuartas.setVisibility(View.VISIBLE);
                    rdSemi.setClickable(true);
                    rdQuartas.setClickable(true);
                } else if ((numGrupos * classificados == 12 || (numGrupos * classificados) == 16)) {
                    rdSemi.setVisibility(View.VISIBLE);
                    rdQuartas.setVisibility(View.VISIBLE);
                    rdOitavas.setVisibility(View.VISIBLE);
                    rdSemi.setClickable(true);
                    rdQuartas.setClickable(true);
                    rdOitavas.setClickable(true);
                }
                return false;
            }
        });
        btnAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeTextInput.setVisibility(View.VISIBLE);
                cidadeTextInput.setVisibility(View.VISIBLE);
                if (rdSim.isChecked()) {
                    premiacaoTextInput.setVisibility(View.VISIBLE);
                }
                lblPergPremiacao.setVisibility(View.VISIBLE);
                rdNao.setVisibility(View.VISIBLE);
                rdSim.setVisibility(View.VISIBLE);
                spAno.setVisibility(View.VISIBLE);
                nomeEditText.setClickable(true);
                cidadeEditText.setClickable(true);
                premiacaoEditText.setClickable(true);
                rdNao.setClickable(true);
                rdSim.setClickable(true);
                spAno.setClickable(true);
                btnAnt.setVisibility(View.INVISIBLE);
                btnAnt.setClickable(false);
                btnProx.setText(R.string.ftc_prox_abrev);
                spFormato.setVisibility(View.INVISIBLE);
                spNumTimes.setVisibility(View.INVISIBLE);
                spGrupos.setVisibility(View.INVISIBLE);
                lblZerar.setVisibility(View.INVISIBLE);
                rdOitavas.setVisibility(View.INVISIBLE);
                rdQuartas.setVisibility(View.INVISIBLE);
                rdSemi.setVisibility(View.INVISIBLE);
                lblPergPendurado.setVisibility(View.VISIBLE);
                penduradosTextInput.setVisibility(View.VISIBLE);
                spFormato.setClickable(false);
                spNumTimes.setClickable(false);
                rdOitavas.setClickable(false);
                rdQuartas.setClickable(false);
                rdSemi.setClickable(false);
                classificadosTextInput.setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(0, true);
                } else {
                    progressBar.setProgress(0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        criarActivity();
    }

    private void criarActivity() {
        Intent it = new Intent(TelaNovoCamp.this, TelaCarregarCamp.class);
        it.putExtra("user", user);
        startActivity(it);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            criarActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean valida(String s) {
        return !s.equals("");
    }

}
