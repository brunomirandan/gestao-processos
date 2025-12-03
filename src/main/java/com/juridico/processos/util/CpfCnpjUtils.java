package com.juridico.processos.util;

public final class CpfCnpjUtils {

    private CpfCnpjUtils() {}

    public static String somenteDigitos(String valor) {
        if (valor == null) return null;
        return valor.replaceAll("\\D", "");
    }

    public static boolean isCpf(String doc) {
        String d = somenteDigitos(doc);
        return d != null && d.length() == 11;
    }

    public static boolean isCnpj(String doc) {
        String d = somenteDigitos(doc);
        return d != null && d.length() == 14;
    }

    public static boolean isValidCpfOrCnpj(String doc) {
        String d = somenteDigitos(doc);
        if (d == null) return false;
        if (d.length() == 11) {
            return isValidCpf(d);
        } else if (d.length() == 14) {
            return isValidCnpj(d);
        }
        return false;
    }

    private static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return false;
        // rejeita todos iguais: 000, 111, ...
        if (cpf.chars().distinct().count() == 1) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito1 == (cpf.charAt(9) - '0') &&
                   digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return false;
        if (cnpj.chars().distinct().count() == 1) return false;

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += (cnpj.charAt(i) - '0') * pesos1[i];
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += (cnpj.charAt(i) - '0') * pesos2[i];
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito1 == (cnpj.charAt(12) - '0') &&
                   digito2 == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}
