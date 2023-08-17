import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calculator {


    private static final int VNDAF_i = 70;
    private static final int VNDBF_i = 30;
    private static final double R_annual_i = 10.8;
    private static final int m_i = 12;
    private static final double tax = 0.1;
    private static final double o = 20; // số tiền rút định kỳ
    private static final double R_ref = 7;

    public static void main(String[] args) {

    }

    private static double PBT_i(double v, double d, int period, double previousPBT_i, int month, int year, String withdrawType) {
        return PB0_i(period, previousPBT_i, v) + DB_i(d, month, year) + IntB_i(period, withdrawType, month, year, previousPBT_i, v, d) +
                IntB_i(period, withdrawType, month, year, previousPBT_i, v, d) + OB_i(withdrawType, month, year, period, v, previousPBT_i);
    }


    private static double R_i() {
        return (Math.pow(1 + R_annual_i, 1/m_i) - 1) * m_i;
    }
    private static double OB_i(String withdrawType, int month, int year, int period, double v, double previousPBT_) {
        if (!positiveKHCL(month, year) && "All".equals(withdrawType)) {
            return PB0_i(period, previousPBT_, v);
        } else if (!positiveKHCL(month, year) && "Monthly".equals(withdrawType)) {
            return Math.min(o, PB0_i(period, previousPBT_, v));
        } else {
            return 0;
        }
    }

    private static double DB_i(double d, int month, int year) {
        return positiveKHCL(month, year) ? d : 0;
    }

    private static double PB0_i(int period, double previousPBT_, double v) {
        return period == 1 ? v : previousPBT_;
    }

    private static double IntB_i(int period, String withdrawType, int month, int year, double previousPBT_, double v, double d) {
        return (PB0_i(period, previousPBT_, v) + DB_i(d, month, year) + OB_i(withdrawType, month, year, period, v, previousPBT_))
                * (RB_i(period)/m_i);
    }

    private static double O_i(int period, String withdrawType, int month, int year, double previousPT_, double v) {
        if (!positiveKHCL(month, year) && "All".equals(withdrawType)) {
            return P0_i(period, previousPT_, v);
        } else if (!positiveKHCL(month, year) && "Monthly".equals(withdrawType)) {
            return Math.min(o/(1-tax), P0_i(period, previousPT_, v));
        } else {
            return 0;
        }
    }

    private static double Int_i(int period, double previousPBT_, double v, double d, String withdrawType, double previousPT_, int month, int year) {
        return (P0_i(period, previousPBT_, v) - O_i(period, withdrawType, month, year, previousPT_, v)) *
                (R_i()/m_i) +
                D_i(d, month, year) * Math.pow((1 + R_i()/m_i), 1 - (0.5 * m_i)/12) - D_i(d, month, year);
    }

    private static double P0_i(int period, double previousPT_, double v) {
        return period == 1 ? v : previousPT_;
    }

    private static double PT_i(int period, double previousPT_, double v, double d, int month, int year, double previousPBT_, String withdrawType) {

        return P0_i(period, previousPT_, v) + D_i(d, month, year) + Int_i(period, previousPBT_, v, d, withdrawType, previousPT_, month, year) +
                O_i(period, withdrawType,month, year, previousPT_, v);
    }

    private static double RB_i(int period) {
        return m_i * (Math.pow(1 + R_ref, 1/m_i - 1));
    }

    private static double D_i(double d, int month, int year) {
        return positiveKHCL(month, year) ? d : 0;
    }

    private static int KHCL(int month, int year) {
        Date currentDate = new Date();
        int currentMonth = currentDate.getMonth();
        int currentYear = currentDate.getYear();
        int result = (currentYear - year) * 12 + (currentMonth - month);
        return result > 0 ? result : 0;
    }

    private static boolean positiveKHCL(int month, int year) {
        return KHCL(month, year) > 0;
    }
}

