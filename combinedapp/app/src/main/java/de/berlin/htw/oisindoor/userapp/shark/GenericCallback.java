package de.berlin.htw.oisindoor.userapp.shark;

/**
 * Callback Interface with one generic method
 * @author Max M
 */
public interface GenericCallback<T>  {
   void onResult(T data);
}
