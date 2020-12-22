package com.example.ehComplicado;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import model.bean.Campeonato;

public class NovoCampFragment extends Fragment {
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
    private int numTimes, numGrupos, ano, classificados, cartoesPendurado, fase;
    private FirebaseUser user;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tela2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RadioButton rdOitavas = view.findViewById(R.id.rdOitavas);
        final RadioButton rdQuartas = view.findViewById(R.id.rdQuartas);
        final RadioButton rdSemi = view.findViewById(R.id.rdSemi);
        final TextView lblZerar = view.findViewById(R.id.lbl_zerar);
        final RadioButton rdSim = view.findViewById(R.id.rdSim);
        final RadioButton rdNao = view.findViewById(R.id.rdNao);
        final Button btnAnt = view.findViewById(R.id.btn_ant);
        final Button btnProx = view.findViewById(R.id.btn_prox);
        final TextView lblPergPremiacao = view.findViewById(R.id.lbl_perg_premiacao);
        final TextView lblPergPendurado = view.findViewById(R.id.lbl_pendurado);
        nomeTextInput = view.findViewById(R.id.nome_text_input);
        nomeEditText = view.findViewById(R.id.nome_edit_text);
        cidadeTextInput = view.findViewById(R.id.cidade_text_input);
        cidadeEditText = view.findViewById(R.id.cidade_edit_text);
        premiacaoEditText = view.findViewById(R.id.premiacao_edit_text);
        premiacaoTextInput = view.findViewById(R.id.premiacao_text_input);
        classificadosTextInput = view.findViewById(R.id.classificados_text_input);
        classificadosEditText = view.findViewById(R.id.classificados_edit_text);
        penduradosTextInput = view.findViewById(R.id.pendurado_text_input);
        penduradosEditText = view.findViewById(R.id.pendurado_edit_text);
        fase = 1;
        progressBar = view.findViewById(R.id.progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();

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
        spGrupos = view.findViewById(R.id.spGrupos);
        spGrupos.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        spGrupos.setClickable(false);
        final ArrayAdapter<String> seisTimesAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.seisTimes)) {
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
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.oitoTimes)) {
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
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.dezTimes)) {
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
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.dozeTimes)) {
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
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.dezesseisTimes)) {
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
        final Spinner spFormato = view.findViewById(R.id.spFormato);
        spFormato.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spFormatoAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.formato)) {
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

        final Spinner spNumTimes = view.findViewById(R.id.spNumTimes);
        spNumTimes.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spNumTimesAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, getResources().getStringArray(R.array.numTimes)) {
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner spAno = view.findViewById(R.id.spAno);
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> anos = new ArrayList<>();
        anos.add(getString(R.string.ftc_ano));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR)));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR) + 1));
        anos.add(String.valueOf(calendar.get(Calendar.YEAR) + 2));
        spAno.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        final ArrayAdapter<String> spAnoAdapter =
                new ArrayAdapter<String>(getContext(), R.layout.personalizado_list_item, anos) {
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
                    ano = Integer.parseInt(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Campeonato camp = getArguments().getParcelable("camp");
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
                if (fase == 1) {
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
                                Toast.makeText(getContext(), R.string.ftc_aviso_ano, Toast.LENGTH_LONG).show();
                            } else {
                                fase++;
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
                                spFormato.setVisibility(View.VISIBLE);
                                spNumTimes.setVisibility(View.VISIBLE);
                                spFormato.setClickable(true);
                                spNumTimes.setClickable(true);
                                if (camp != null) {
                                    if (camp.getFormato().equals(getString(R.string.fasedegrupos))) {
                                        spGrupos.setVisibility(View.VISIBLE);
                                        classificadosTextInput.setVisibility(View.VISIBLE);
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressBar.setProgress(33, true);
                                } else {
                                    progressBar.setProgress(33);
                                }
                            }
                        }
                    }
                } else if (fase == 2) {
                    classificados = Integer.parseInt(classificadosEditText.getText().toString());
                    if (formato.isEmpty()) {
                        Toast.makeText(getContext(), R.string.ftc_aviso_formato, Toast.LENGTH_LONG).show();
                    } else if (numTimes == 0) {
                        Toast.makeText(getContext(), R.string.ftc_aviso_num_times, Toast.LENGTH_LONG).show();
                    } else if (spGrupos.getVisibility() == View.VISIBLE && spGrupos.getSelectedItemPosition() == 0) {
                        Toast.makeText(getContext(), R.string.ftc_aviso_num_grupos, Toast.LENGTH_LONG).show();
                    } else if (formato.equals(getString(R.string.fasedegrupos)) && classificados == 0) {
                        Toast.makeText(getContext(), R.string.ftc_aviso_classificados, Toast.LENGTH_SHORT).show();
                    } else if (spGrupos.getVisibility() == View.VISIBLE && (numTimes / numGrupos) < classificados) {
                        Toast.makeText(getContext(), R.string.ftc_aviso_classificados2, Toast.LENGTH_SHORT).show();
                    } else if (classificados < 0) {
                        Toast.makeText(getContext(), "Deve haver pelo menos um classificado por grupo.", Toast.LENGTH_SHORT).show();
                    } else {
                        spFormato.setVisibility(View.INVISIBLE);
                        spNumTimes.setVisibility(View.INVISIBLE);
                        classificadosTextInput.setVisibility(View.INVISIBLE);
                        spGrupos.setVisibility(View.INVISIBLE);
                        lblZerar.setVisibility(View.VISIBLE);
                        lblPergPendurado.setVisibility(View.VISIBLE);
                        penduradosTextInput.setVisibility(View.VISIBLE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBar.setProgress(66, true);
                        } else {
                            progressBar.setProgress(66);
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
                        btnProx.setText(R.string.ftc_revisar);
                        fase++;
                    }
                } else if (fase == 3) {
                    cartoesPendurado = Integer.parseInt(String.valueOf(penduradosEditText.getText()));
                    if (cartoesPendurado <= 1) {
                        Toast.makeText(getContext(), "O número de cartôes para suspensão deve ser superior a 1", Toast.LENGTH_SHORT).show();
                    } else {
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
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        TelaRevisao telaRevisao = new TelaRevisao();
                        Bundle data = new Bundle();
                        data.putParcelable("user", user);
                        data.putParcelable("camp", camp);
                        telaRevisao.setArguments(data);
                        TelaCarregarCamp tela = (TelaCarregarCamp) getActivity();
                        tela.openFragment(telaRevisao);
                    }
                }
            }
        });
        btnAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fase == 2) {
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
                    spFormato.setClickable(false);
                    spNumTimes.setClickable(false);
                    classificadosTextInput.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(0, true);
                    } else {
                        progressBar.setProgress(0);
                    }
                    fase--;
                } else if (fase == 3) {
                    spFormato.setVisibility(View.VISIBLE);
                    spNumTimes.setVisibility(View.VISIBLE);
                    spGrupos.setVisibility(View.VISIBLE);
                    classificadosTextInput.setVisibility(View.VISIBLE);
                    lblZerar.setVisibility(View.INVISIBLE);
                    rdOitavas.setVisibility(View.INVISIBLE);
                    rdQuartas.setVisibility(View.INVISIBLE);
                    rdSemi.setVisibility(View.INVISIBLE);
                    lblPergPendurado.setVisibility(View.INVISIBLE);
                    penduradosTextInput.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(33, true);
                    } else {
                        progressBar.setProgress(33);
                    }
                    btnProx.setText(R.string.ftc_prox_abrev);
                    fase--;
                }
            }
        });
    }

    public static NovoCampFragment newInstance() {
        return new NovoCampFragment();
    }

    private boolean valida(String s) {
        return !s.equals("");
    }
}
