package modelo;

import java.util.ArrayList;
import java.util.List;
import jogo.Movimento;
import jogo.Tabuleiro;

public class Dama extends Peca {

    public Dama(Cor corDaPeca) {
        super(corDaPeca);
    }

    @Override
    public String getSimbolo() {
        return getCor().getSimbolo().toUpperCase();
    }

    @Override
    public List<Movimento> getMovimentosSimples(Tabuleiro tabuleiro, Posicao origem) {
        List<Movimento> movimentos = new ArrayList<>();
        int[][] direcoes = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

        for (int[] dir : direcoes) {
            int l = origem.getLinha() + dir[0];
            int c = origem.getColuna() + dir[1];

            while (true) {
                Posicao destino = new Posicao(l, c);

                if (!destino.isDentroDoTabuleiro()) break;
                if (tabuleiro.getPeca(destino) != null) break;

                movimentos.add(new Movimento(origem, destino));

                l += dir[0];
                c += dir[1];
            }
        }

        return movimentos;
    }

    @Override
    public List<List<Movimento>> getSequenciasCaptura(Tabuleiro tabuleiro, Posicao origem) {
        List<List<Movimento>> todasSequencias = new ArrayList<>();
        int[][] direcoes = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

        for (int[] dir : direcoes) {
            int l = origem.getLinha() + dir[0];
            int c = origem.getColuna() + dir[1];

            while (true) {
                Posicao posAtual = new Posicao(l, c);

                if (!posAtual.isDentroDoTabuleiro()) break;

                Peca pecaEncontrada = tabuleiro.getPeca(posAtual);

                if (pecaEncontrada == null) {
                    l += dir[0];
                    c += dir[1];
                    continue;
                }

                if (!this.podeCapturar(pecaEncontrada)) {
                    break;
                }

                int pousoL = l + dir[0];
                int pousoC = c + dir[1];

                while (true) {
                    Posicao posPouso = new Posicao(pousoL, pousoC);

                    if (!posPouso.isDentroDoTabuleiro()
                            || tabuleiro.getPeca(posPouso) != null) {
                        break;
                    }

                    Movimento saltoAtual = new Movimento(origem, posPouso);

                    pecaEncontrada.marcarCapturada();
                    tabuleiro.removerPeca(origem);
                    tabuleiro.colocarPeca(posPouso, this);

                    List<List<Movimento>> continuacoes =
                            getSequenciasCaptura(tabuleiro, posPouso);

                    tabuleiro.removerPeca(posPouso);
                    tabuleiro.colocarPeca(origem, this);
                    pecaEncontrada.desmarcarCapturada();

                    if (continuacoes.isEmpty()) {
                        List<Movimento> seq = new ArrayList<>();
                        seq.add(saltoAtual);
                        todasSequencias.add(seq);
                    } else {
                        for (List<Movimento> resto : continuacoes) {
                            List<Movimento> seq = new ArrayList<>();
                            seq.add(saltoAtual);
                            seq.addAll(resto);
                            todasSequencias.add(seq);
                        }
                    }

                    pousoL += dir[0];
                    pousoC += dir[1];
                }

                break;
            }
        }

        return todasSequencias;
    }
}
