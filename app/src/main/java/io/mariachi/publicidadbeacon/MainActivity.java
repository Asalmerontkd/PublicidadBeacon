package io.mariachi.publicidadbeacon;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    BluetoothAdapter bluetoothAdapter;
    BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null) // If null -> cerrar la aplicación, el dispositivo no tiene bluetooth
        {
            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setMessage("Bluetooth no encontrado en el dispositivo.");
            alerta.setTitle("Bluetooth no encontrado.");
            alerta.setIcon(android.R.drawable.ic_dialog_alert);
            alerta.setCancelable(false);
            alerta.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Cerrar la aplicació por que no se encontró bluetooth.
                    finish();
                }
            });
            alerta.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) //if Bluetooth no esta activo
        {
            AlertDialog.Builder activaBT = new AlertDialog.Builder(this);
            activaBT.setMessage("El bluetooth no se encuentra activo.\n¿Desea activarlo?");
            activaBT.setTitle("Bluetooth desactivado.");
            activaBT.setIcon(android.R.drawable.ic_dialog_alert);
            activaBT.setCancelable(false);
            activaBT.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Activar el bluetooth
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothIntent, RESULT_OK);
                }
            });
            activaBT.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Si no se activa el bluetooth, salir de la aplicación
                    finish();
                }
            });
            activaBT.show();
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("myBeaons", null, null, null);

        //Monitorea en la region por beacons
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        //Cuando encuentra un beacon comienza a obtener sus datos como la distancia
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for (final Beacon beacon : collection)
                {
                    double precision;
                    double ratio = (beacon.getRssi()*1.0)/beacon.getTxPower();
                    if (ratio<1.0)
                    {
                        precision=Math.pow(ratio, 10);
                    }
                    else
                    {
                        precision=(0.89976)*Math.pow(ratio,7.7095) + 0.111;
                    }

                    if (precision<1) //SI se encuentra a menos de X metros se dispara la notificacion
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                launchPub(beacon.getBluetoothAddress());
                            }
                        });
                    }
                }
            }
        });

        try
        {
            beaconManager.startMonitoringBeaconsInRegion(region);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
        }
    }

    int contA = 0;
    int contB = 0;
    public void launchPub(String mac)
    {
        Log.d("Beacon",mac);

        if (contA<1 && mac.equals("01:17:C5:55:D2:F1")) //01:17:C5:55:D2:F1 promocion A
        {
            //TODO intent promocion A
            Intent promoA = new Intent(this, Promocion.class);
            promoA.putExtra("imgUrl", "http://vitrosdr.com/tienda/media/wysiwyg/1-Anuncio-Vertical-40-.png");
            startActivity(promoA);
            contA++;
        }
        else if (contB<1 && mac.equals("01:17:C5:58:3C:89")) //01:17:C5:58:3C:89 promocion B
        {
            //TODO intent promocion B
            Intent promoA = new Intent(this, Promocion.class);
            promoA.putExtra("imgUrl", "https://tiendatelcel.com.mx//static/uploaded/plan_promocion/2013/DICIEMBRE-2013/comparte-navidad-2/S4--500plus-comparte-navidad.jpg");
            startActivity(promoA);
            contB++;
        }
    }
}
